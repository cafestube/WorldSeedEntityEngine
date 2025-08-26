package net.worldseed.multipart;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minestom.server.MinecraftServer;
import net.minestom.server.ServerProcess;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.collision.Shape;
import net.minestom.server.collision.SweepResult;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.BlockFace;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.events.AnimationCompleteEvent;
import net.worldseed.multipart.events.ModelEvent;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.model_bones.*;
import net.worldseed.multipart.model_bones.bone_types.RideableBone;
import net.worldseed.multipart.model_bones.display_entity.RootBoneEntity;
import net.worldseed.multipart.model_bones.entity.AbstractBoneEntity;
import net.worldseed.multipart.model_bones.misc.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GenericModelImpl extends AbstractGenericModelImpl<Player> implements MinestomModel {

    private final EventNode<@NotNull ModelEvent> eventNode;
    protected Instance instance;

    private static final EventFilter<@NotNull ModelEvent, @NotNull MinestomModel> MODEL_FILTER = EventFilter.from(ModelEvent.class, MinestomModel.class, ModelEvent::model);

    public GenericModelImpl(ModelRegistry registry, String modelId) {
        super(registry, modelId);

        final ServerProcess process = MinecraftServer.process();
        //noinspection ConstantValue
        if (process != null) {
            this.eventNode = process.eventHandler().map(this, MODEL_FILTER);
        } else {
            // Local nodes require a server process
            this.eventNode = null;
        }
    }

    @Override
    public ModelRegistry getModelRegistry() {
        return (ModelRegistry) super.getModelRegistry();
    }

    @Override
    public RootBoneEntity getModelRoot() {
        return (RootBoneEntity) super.getModelRoot();
    }

    public void setPosition(net.minestom.server.coordinate.Pos position) {
        setPosition(PositionConversion.fromMinestom(position));
    }

    @Override
    public @NotNull EventNode<@NotNull ModelEvent> eventNode() {
        return eventNode;
    }

    public void triggerAnimationEnd(String animation, AnimationHandler.AnimationDirection direction) {
        MinecraftServer.getGlobalEventHandler().call(new AnimationCompleteEvent(this, animation, direction));
    }

    public void init(@Nullable Instance instance, @NotNull Pos position) {
        init(instance, position, 1);
    }

    @Override
    public EntityFactory<Player> getEntityFactory() {
        return MinestomEntityFactory.INSTANCE;
    }


    public void init(Instance instance, net.minestom.server.coordinate.Pos pos) {
        init(instance, PositionConversion.fromMinestom(pos));
    }

    public void init(Instance instance, net.minestom.server.coordinate.Pos pos, float scale) {
        init(instance, PositionConversion.fromMinestom(pos), scale);
    }

    public void init(@Nullable Instance instance, @NotNull Pos position, float scale) {
        this.instance = instance;
        super.init(position, scale);

        //TODO: Make this behave like the original implementation
        for (ModelBone<Player> modelBonePart : this.parts.values()) {
            AbstractBoneEntity<Player> entity = modelBonePart.getEntity();
            if(entity instanceof BoneEntity be) {
                be.setInstance(instance, modelBonePart.calculatePosition()).join();
            }

            for (ModelBone<Player> child : modelBonePart.getChildren()) {
                AbstractBoneEntity<Player> childEntity = child.getEntity();
                if(childEntity instanceof BoneEntity be) {
                    be.setInstance(instance, child.calculatePosition()).join();
                }
            }
        }
        this.getModelRoot().setInstance(instance, position);
        draw();

        this.getParts().stream()
                .map(ModelBone::getEntity)
                .filter(e -> e instanceof BoneEntity b && (b.getEntityType() == EntityType.ITEM_DISPLAY || b.getEntityType() == EntityType.TEXT_DISPLAY))
                .forEach(playerAbstractBoneEntity -> getModelRoot().addPassenger((BoneEntity) playerAbstractBoneEntity));
    }

    protected void registerBoneSuppliers() {
        super.registerBoneSuppliers();
        boneSuppliers.put(name -> name.equals("nametag") || name.equals("tag_name"), (info) -> new ModelBoneNametag(info.pivot(), info.name(), info.rotation(), this, info.scale()));
        boneSuppliers.put(name -> name.contains("hitbox"), (info) -> {
            if (info.cubes().isEmpty()) return null;

            var cube = info.cubes().get(0);
            JsonArray sizeArray = cube.getAsJsonObject().get("size").getAsJsonArray();
            JsonArray p = cube.getAsJsonObject().get("pivot").getAsJsonArray();

            Point sizePoint = new Vec(sizeArray.get(0).getAsFloat(), sizeArray.get(1).getAsFloat(), sizeArray.get(2).getAsFloat());
            Point pivotPoint = new Vec(p.get(0).getAsFloat(), p.get(1).getAsFloat(), p.get(2).getAsFloat());

            var newOffset = pivotPoint.mul(-1, 1, 1);
            return new ModelBoneHitbox(info.pivot(), info.name(), info.rotation(), this, newOffset, sizePoint.x(), sizePoint.y(), info.cubes(), true, info.scale());
        });
        boneSuppliers.put(name -> name.contains("seat"), (info) -> new ModelBoneSeat(info.pivot(), info.name(), info.rotation(), this, info.scale()));
    }

    public Instance getInstance() {
        return instance;
    }

    @Override
    public boolean isFaceFull(@NotNull BlockFace face) {
        return true;
    }

    @Override
    public boolean isOccluded(@NotNull Shape shape, @NotNull BlockFace blockFace) {
        return false;
    }

    @Override
    public net.minestom.server.coordinate.@NotNull Point relativeStart() {
        net.minestom.server.coordinate.Pos currentPosition = PositionConversion.asMinestom(getPosition());
        net.minestom.server.coordinate.Point p = currentPosition;

        for (ModelBone<Player> bone : this.parts.values()) {
            for (var part : bone.getChildren()) {
                var entity = part.getEntity();
                if(!(entity instanceof BoneEntity boneEntity)) continue;

                var absoluteStart = boneEntity.relativeStart().add(boneEntity.getPosition());

                if (p.x() > absoluteStart.x()) p = p.withX(absoluteStart.x());
                if (p.y() > absoluteStart.y()) p = p.withY(absoluteStart.y());
                if (p.z() > absoluteStart.z()) p = p.withZ(absoluteStart.z());
            }
        }

        return p.sub(currentPosition);
    }

    @Override
    public @NotNull net.minestom.server.coordinate.Point relativeEnd() {
        net.minestom.server.coordinate.Pos currentPosition = PositionConversion.asMinestom(getPosition());
        net.minestom.server.coordinate.Point p = currentPosition;

        for (var bone : this.parts.values()) {
            for (var part : bone.getChildren()) {
                var entity = part.getEntity();
                if(!(entity instanceof BoneEntity boneEntity)) continue;
                var absoluteStart = boneEntity.relativeEnd().add(boneEntity.getPosition());

                if (p.x() < absoluteStart.x()) p = p.withX(absoluteStart.x());
                if (p.y() < absoluteStart.y()) p = p.withY(absoluteStart.y());
                if (p.z() < absoluteStart.z()) p = p.withZ(absoluteStart.z());
            }
        }

        return p.sub(currentPosition);
    }

    @Override
    public boolean intersectBox(@NotNull net.minestom.server.coordinate.Point point, @NotNull BoundingBox boundingBox) {
        var pos = PositionConversion.asMinestom(getPosition());

        for (var bone : this.parts.values()) {
            for (var part : bone.getChildren()) {
                AbstractBoneEntity<Player> entity = part.getEntity();
                if (entity instanceof BoneEntity boneEntity && boundingBox.intersectEntity(pos.sub(point), boneEntity)) return true;
            }
        }
        return false;
    }

    @Override
    @ApiStatus.Experimental
    public boolean intersectBoxSwept(@NotNull net.minestom.server.coordinate.Point rayStart, @NotNull net.minestom.server.coordinate.Point rayDirection, @NotNull net.minestom.server.coordinate.Point shapePos, @NotNull BoundingBox moving, @NotNull SweepResult finalResult) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void mountEntity(String name, Entity entity) {
        if (this.parts.get(name) instanceof RideableBone rideable) rideable.addPassenger(entity);
    }

    @Override
    public void dismountEntity(String name, Entity entity) {
        if (this.parts.get(name) instanceof RideableBone rideable) rideable.removePassenger(entity);
    }

    @Override
    public Set<Entity> getPassengers(String name) {
        if (this.parts.get(name) instanceof RideableBone rideable) return rideable.getPassengers();
        return Collections.emptySet();
    }
}
