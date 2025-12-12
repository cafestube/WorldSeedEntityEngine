package net.worldseed.multipart;

import net.minestom.server.MinecraftServer;
import net.minestom.server.ServerProcess;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.collision.Shape;
import net.minestom.server.collision.SweepResult;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.BlockFace;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.animations.AnimationHandlerImpl;
import net.worldseed.multipart.events.AnimationCompleteEvent;
import net.worldseed.multipart.events.ModelEvent;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.entity.*;
import net.worldseed.multipart.entity.bone_types.RideableBone;
import net.worldseed.multipart.entity.display_entity.MinestomRootBoneEntity;
import net.worldseed.multipart.entity.entity.BoneEntity;
import net.worldseed.multipart.entity.misc.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GenericModelImpl extends AbstractGenericModelImpl<Player> implements MinestomModel {

    private final EventNode<@NotNull ModelEvent> eventNode;
    protected Instance instance;
    private AnimationHandler animationHandler;

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
    public void destroy() {
        super.destroy();
        this.animationHandler.destroy();
    }

    @Override
    public boolean isSpawned() {
        return getInstance() != null;
    }

    @Override
    public ModelRegistry getModelRegistry() {
        return (ModelRegistry) super.getModelRegistry();
    }

    @Override
    public MinestomRootBoneEntity getModelRoot() {
        return (MinestomRootBoneEntity) super.getModelRoot();
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
    public AnimationHandler getAnimationHandler() {
        return animationHandler;
    }

    @Override
    public ModelPlatform<Player> getModelPlatform() {
        return MinestomModelPlatform.INSTANCE;
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
    }

    @Override
    protected void init(@NotNull Pos position, float scale) {
        super.init(position, scale);

        if(this.animationHandler != null) {
            this.animationHandler.destroy();
        }
        this.animationHandler = new AnimationHandlerImpl<>(this);
    }

    protected void registerBoneSuppliers() {
        super.registerBoneSuppliers();
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
                if(!(entity instanceof MinestomBoneEntity boneEntity)) continue;

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
                if(!(entity instanceof MinestomBoneEntity boneEntity)) continue;
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
                BoneEntity<Player> entity = part.getEntity();
                if (entity instanceof MinestomBoneEntity boneEntity && boundingBox.intersectEntity(pos.sub(point), boneEntity)) return true;
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
