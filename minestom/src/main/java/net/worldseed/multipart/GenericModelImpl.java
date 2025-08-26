package net.worldseed.multipart;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.RGBLike;
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
import net.worldseed.multipart.math.PositionParser;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.model_bones.*;
import net.worldseed.multipart.model_bones.bone_types.HeadBone;
import net.worldseed.multipart.model_bones.display_entity.ModelBoneHeadDisplay;
import net.worldseed.multipart.model_bones.display_entity.ModelBonePartDisplay;
import net.worldseed.multipart.model_bones.display_entity.RootBoneEntity;
import net.worldseed.multipart.model_bones.entity.AbstractBoneEntity;
import net.worldseed.multipart.model_bones.misc.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class GenericModelImpl implements GenericModel {
    protected final LinkedHashMap<String, ModelBone<Player, GenericModel>> parts = new LinkedHashMap<>();
    protected final Set<ModelBone<Player, GenericModel>> viewableBones = new LinkedHashSet<>();

    private final Collection<ModelBone<Player, GenericModel>> additionalBones = new ArrayList<>();
    private final Set<Player> viewers = ConcurrentHashMap.newKeySet();
    private final EventNode<ModelEvent> eventNode;
    private final Map<Player, RGBLike> playerGlowColors = Collections.synchronizedMap(new WeakHashMap<>());
    protected Instance instance;
    protected Pos position;
    private double globalRotation;
    private double pitch;
    private BoneEntity rootEntity;

    public void setPosition(net.minestom.server.coordinate.Pos position) {
        setPosition(PositionConversion.fromMinestom(position));
    }

    protected record ModelBoneInfo(String name, Point pivot, Point rotation, JsonArray cubes, GenericModel model,
                                   float scale) {
    }

    protected final Map<Predicate<String>, Function<ModelBoneInfo, @Nullable ModelBone<Player, GenericModel>>> boneSuppliers = new LinkedHashMap<>();
    Function<ModelBoneInfo, ModelBone<Player, GenericModel>> defaultBoneSupplier = (info) -> new ModelBonePartDisplay<>(info.pivot, info.name, info.rotation, info.model, info.scale);

    private static final EventFilter<ModelEvent, GenericModel> MODEL_FILTER = EventFilter.from(ModelEvent.class, GenericModel.class, ModelEvent::model);

    private final ModelRegistry modelRegistry;
    private final String modelId;

    public GenericModelImpl(ModelRegistry registry, String modelId) {
        this.modelRegistry = registry;
        this.modelId = modelId;

        final ServerProcess process = MinecraftServer.process();
        if (process != null) {
            this.eventNode = process.eventHandler().map(this, MODEL_FILTER);
        } else {
            // Local nodes require a server process
            this.eventNode = null;
        }

        registerBoneSuppliers();
        this.rootEntity = new RootBoneEntity(this);
    }

    @Override
    public @NotNull EventNode<ModelEvent> eventNode() {
        return eventNode;
    }

    @Override
    public double getGlobalRotation() {
        return globalRotation;
    }

    @Override
    public double getPitch(){
        return pitch;
    }

    public void setGlobalRotation(double yaw) {
        setGlobalRotation(yaw, 0.0f);
    }

    public void setGlobalRotation(double yaw, double pitch) {
        this.globalRotation = yaw;
        this.pitch = pitch;

        this.viewableBones.forEach(part -> {
            part.setGlobalRotation(yaw, pitch);
            part.setGlobalRotation(yaw, pitch);
        });
    }

    @Override
    public Pos getPosition() {
        return position;
    }

    public void setPosition(Pos pos) {
        this.position = pos;
        this.rootEntity.teleport(PositionConversion.asMinestom(position.withView(0, 0)));
        this.parts.values().forEach(part -> part.teleport(pos));
    }

    @Override
    public BoneEntity getModelRoot() {
        return this.rootEntity;
    }

    public void triggerAnimationEnd(String animation, AnimationHandler.AnimationDirection direction) {
        MinecraftServer.getGlobalEventHandler().call(new AnimationCompleteEvent(this, animation, direction));
    }

    public void init(@Nullable Instance instance, @NotNull Pos position) {
        init(instance, position, 1);
    }

    @Override
    public ModelRegistry getModelRegistry() {
        return modelRegistry;
    }

    @Override
    public String getId() {
        return this.modelId;
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
        this.position = position;

        JsonObject loadedModel = this.getModelRegistry().getOrLoadGeometry(getId());

        this.setGlobalRotation(position.yaw());

        loadBones(loadedModel, scale);

        for (ModelBone<Player, GenericModel> modelBonePart : this.parts.values()) {
            if (modelBonePart instanceof ModelBoneViewable)
                viewableBones.add(modelBonePart);

            AbstractBoneEntity<Player> entity = modelBonePart.getEntity();
            if(entity instanceof BoneEntity be) {
                be.setInstance(instance, modelBonePart.calculatePosition()).join();
            }

            for (ModelBone<Player, GenericModel> child : modelBonePart.getChildren()) {
                AbstractBoneEntity<Player> childEntity = child.getEntity();
                if(childEntity instanceof BoneEntity be) {
                    be.setInstance(instance, child.calculatePosition()).join();
                }
            }
        }
        this.rootEntity.setInstance(instance, position);
        draw();

        this.getParts().stream()
                .map(ModelBone::getEntity)
                .filter(e -> e instanceof BoneEntity b && (b.getEntityType() == EntityType.ITEM_DISPLAY || b.getEntityType() == EntityType.TEXT_DISPLAY))
                .forEach(playerAbstractBoneEntity -> rootEntity.addPassenger((BoneEntity) playerAbstractBoneEntity));

        this.setState("normal");
    }

    @Override
    public void setGlobalScale(float scale) {
        for (ModelBone modelBonePart : this.parts.values()) {
            modelBonePart.setGlobalScale(scale);
        }
    }

    protected void registerBoneSuppliers() {
        boneSuppliers.put(name -> name.equals("nametag") || name.equals("tag_name"), (info) -> new ModelBoneNametag(info.pivot, info.name, info.rotation, info.model, info.scale));
        boneSuppliers.put(name -> name.contains("hitbox"), (info) -> {
            if (info.cubes.isEmpty()) return null;

            var cube = info.cubes.get(0);
            JsonArray sizeArray = cube.getAsJsonObject().get("size").getAsJsonArray();
            JsonArray p = cube.getAsJsonObject().get("pivot").getAsJsonArray();

            Point sizePoint = new Vec(sizeArray.get(0).getAsFloat(), sizeArray.get(1).getAsFloat(), sizeArray.get(2).getAsFloat());
            Point pivotPoint = new Vec(p.get(0).getAsFloat(), p.get(1).getAsFloat(), p.get(2).getAsFloat());

            var newOffset = pivotPoint.mul(-1, 1, 1);
            return new ModelBoneHitbox(info.pivot, info.name, info.rotation, info.model, newOffset, sizePoint.x(), sizePoint.y(), info.cubes, true, info.scale);
        });
        boneSuppliers.put(name -> name.contains("vfx"), (info) -> new ModelBoneVFX<>(info.pivot, info.name, info.rotation, info.model, info.scale));
        boneSuppliers.put(name -> name.contains("seat"), (info) -> new ModelBoneSeat(info.pivot, info.name, info.rotation, info.model, info.scale));
        boneSuppliers.put(name -> name.equals("head"), (info) -> new ModelBoneHeadDisplay<>(info.pivot, info.name, info.rotation, info.model, info.scale));
    }

    protected void loadBones(JsonObject loadedModel, float scale) {
        // Build bones
        for (JsonElement bone : loadedModel.get("minecraft:geometry").getAsJsonArray().get(0).getAsJsonObject().get("bones").getAsJsonArray()) {
            JsonElement pivot = bone.getAsJsonObject().get("pivot");
            String name = bone.getAsJsonObject().get("name").getAsString();

            Point boneRotation = PositionParser.getPos(bone.getAsJsonObject().get("rotation")).orElse(Pos.ZERO).mul(-1, -1, 1);
            Point pivotPos = PositionParser.getPos(pivot).orElse(Pos.ZERO).mul(-1, 1, 1);

            boolean found = false;
            for (Map.Entry<Predicate<String>, Function<ModelBoneInfo, @Nullable ModelBone<Player, GenericModel>>> entry : this.boneSuppliers.entrySet()) {
                var predicate = entry.getKey();
                var supplier = entry.getValue();

                if (predicate.test(name)) {
                    var modelBonePart = supplier.apply(new ModelBoneInfo(name, pivotPos, boneRotation, bone.getAsJsonObject().getAsJsonArray("cubes"), this, scale));

                    additionalBones.addAll(modelBonePart.getChildren());
                    parts.put(name, modelBonePart);
                    found = true;

                    break;
                }
            }

            if (!found) {
                var modelBonePart = defaultBoneSupplier.apply(new ModelBoneInfo(name, pivotPos, boneRotation, bone.getAsJsonObject().getAsJsonArray("cubes"), this, scale));

                additionalBones.addAll(modelBonePart.getChildren());
                parts.put(name, modelBonePart);
            }
        }

        // Link parents
        for (JsonElement bone : loadedModel.get("minecraft:geometry").getAsJsonArray().get(0).getAsJsonObject().get("bones").getAsJsonArray()) {
            String name = bone.getAsJsonObject().get("name").getAsString();
            JsonElement parent = bone.getAsJsonObject().get("parent");
            String parentString = parent == null ? null : parent.getAsString();

            if (parentString != null) {
                ModelBone<Player, GenericModel> child = this.parts.get(name);

                if (child == null) continue;
                ModelBone<Player, GenericModel> parentBone = this.parts.get(parentString);

                child.setParent(parentBone);
                parentBone.addChild(child);
            }
        }
    }

    public Instance getInstance() {
        return instance;
    }

    public void setState(String state) {
        for (ModelBone<Player, GenericModel> part : viewableBones) {
            part.setState(state);
        }
    }

    public ModelBone<Player, GenericModel> getPart(String boneName) {
        return this.parts.get(boneName);
    }

    public void draw() {
        for (ModelBone<Player, GenericModel> modelBonePart : this.parts.values()) {
            if (modelBonePart.getParent() == null)
                modelBonePart.draw();
        }
    }

    public void destroy() {
        for (ModelBone<Player, GenericModel> modelBonePart : this.parts.values()) {
            modelBonePart.destroy();
        }
        this.rootEntity.remove();

        this.viewableBones.clear();
        this.parts.clear();
    }

    @Override
    public Point getVFX(String name) {
        ModelBone<Player, GenericModel> found = this.parts.get(name);
        if (found == null) return null;
        return found.getPosition();
    }

    @Override
    public void setHeadRotation(String name, double rotation) {
        ModelBone<Player, GenericModel> found = this.parts.get(name);
        if (found instanceof HeadBone<Player, GenericModel> head) head.setRotation(rotation);
    }

    public @NotNull List<ModelBone<Player, GenericModel>> getParts() {
        ArrayList<ModelBone<Player, GenericModel>> res = this.parts.values().stream()
                .filter(Objects::nonNull)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        res.addAll(this.additionalBones);
        return res;
    }

    public Pos getPivot() {
        return Pos.ZERO;
    }

    public Pos getGlobalOffset() {
        return Pos.ZERO;
    }

    public Point getDiff(String boneName) {
        return modelRegistry.diffMappings.get(getId() + "/" + boneName);
    }

    public Point getOffset(String boneName) {
        return modelRegistry.offsetMappings.get(getId() + "/" + boneName);
    }
//
//    @Override
//    public boolean isViewer(@NotNull Player player) {
//        return this.viewers.contains(player);
//    }
//
//    @Override
//    public void sendPacketToViewers(@NotNull SendablePacket packet) {
//        for (Player viewer : this.viewers) {
//            viewer.sendPacket(packet);
//        }
//    }
//
//    @Override
//    public void sendPacketsToViewers(@NotNull Collection<SendablePacket> packets) {
//        for (Player viewer : this.viewers) {
//            for (SendablePacket packet : packets) {
//                viewer.sendPacket(packet);
//            }
//        }
//    }
//
//    @Override
//    public void sendPacketsToViewers(@NotNull SendablePacket... packets) {
//        for (Player viewer : this.viewers) {
//            for (SendablePacket packet : packets) {
//                viewer.sendPacket(packet);
//            }
//        }
//    }
//
//    @Override
//    public void sendPacketToViewersAndSelf(@NotNull SendablePacket packet) {
//        sendPacketToViewers(packet);
//    }
//
//    @Override
//    public @NotNull Audience getViewersAsAudience() {
//        return Audience.audience(viewers);
//    }
//
//    @Override
//    public @NotNull Iterable<? extends Audience> getViewersAsAudiences() {
//        return List.of(getViewersAsAudience());
//    }

    @Override
    public boolean addViewer(@NotNull Player player) {
        getParts().forEach(part -> part.addViewer(player));
        this.rootEntity.addViewer(player);

        var foundPlayerGlowing = this.playerGlowColors.get(player);
        if (foundPlayerGlowing != null)
            this.viewableBones.forEach(part -> part.setGlowing(player, foundPlayerGlowing));

        return this.viewers.add(player);
    }

    @Override
    public boolean removeViewer(@NotNull Player player) {
        getParts().forEach(part -> part.removeViewer(player));
        this.rootEntity.removeViewer(player);

        return this.viewers.remove(player);
    }

    @Override
    public @NotNull Set<@NotNull Player> getViewers() {
        return Set.copyOf(this.viewers);
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
    public net.minestom.server.coordinate.Point relativeStart() {
        net.minestom.server.coordinate.Pos currentPosition = PositionConversion.asMinestom(getPosition());
        net.minestom.server.coordinate.Point p = currentPosition;

        for (ModelBone<Player, GenericModel> bone : this.parts.values()) {
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
    public void setGlowing(RGBLike color) {
        this.viewableBones.forEach(part -> part.setGlowing(color));
    }

    @Override
    public void removeGlowing() {
        this.viewableBones.forEach(ModelBone::removeGlowing);
    }

    @Override
    public void setGlowing(Player player, RGBLike color) {
        this.playerGlowColors.put(player, color);
        this.viewableBones.forEach(part -> part.setGlowing(player, color));
    }

    @Override
    public void removeGlowing(Player player) {
        this.playerGlowColors.remove(player);
        this.viewableBones.forEach(part -> part.removeGlowing(player));
    }

    @Override
    public void attachModel(GenericModel model, String boneName) {
        ModelBone<Player, GenericModel> bone = this.parts.get(boneName);
        if (bone != null) bone.attachModel(model);
    }

    @Override
    public void detachModel(GenericModel model, String boneName) {
        ModelBone<Player, GenericModel> bone = this.parts.get(boneName);
        if (bone != null) bone.detachModel(model);
    }

    @Override
    public Map<String, List<GenericModel>> getAttachedModels() {
        Map<String, List<GenericModel>> attached = new HashMap<>();
        for (ModelBone<Player, GenericModel> part : this.parts.values()) {
            attached.put(part.getName(), part.getAttachedModels());
        }
        return attached;
    }

    @Override
    public void mountEntity(String name, Entity entity) {
//        if (this.parts.get(name) instanceof RideableBone rideable) rideable.addPassenger(entity);
    }

    @Override
    public void dismountEntity(String name, Entity entity) {
//        if (this.parts.get(name) instanceof RideableBone rideable) rideable.removePassenger(entity);
    }

    @Override
    public Set<Entity> getPassengers(String name) {
//        if (this.parts.get(name) instanceof RideableBone rideable) return rideable.getPassengers();
        return Collections.emptySet();
    }

    @Override
    public void setNametag(String name, Component nametag) {
        if (this.parts.get(name) instanceof ModelBoneNametag nametagBone) nametagBone.setNametag(nametag);
    }

    @Override
    public Component getNametag(String name) {
        if (this.parts.get(name) instanceof ModelBoneNametag nametagBone) return nametagBone.getNametag();
        return null;
    }
}
