package net.worldseed.multipart;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.RGBLike;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.PositionParser;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.entity.*;
import net.worldseed.multipart.entity.bone_types.HeadBone;
import net.worldseed.multipart.entity.bone_types.NametagBone;
import net.worldseed.multipart.entity.display_entity.ModelBoneHeadDisplay;
import net.worldseed.multipart.entity.display_entity.ModelBonePartDisplay;
import net.worldseed.multipart.entity.entity.BoneEntity;
import net.worldseed.multipart.entity.entity.ItemDisplayBoneEntity;
import net.worldseed.multipart.entity.entity.RootBoneEntity;
import net.worldseed.multipart.entity.entity.TextDisplayBoneEntity;
import net.worldseed.multipart.entity.misc.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractGenericModelImpl<TViewer> implements GenericModel<TViewer> {
    protected final LinkedHashMap<String, ModelBone<TViewer>> parts = new LinkedHashMap<>();
    protected final Set<ModelBone<TViewer>> viewableBones = new LinkedHashSet<>();

    private final Collection<ModelBone<TViewer>> additionalBones = new ArrayList<>();
    private final Set<TViewer> viewers = ConcurrentHashMap.newKeySet();
    private final Map<TViewer, RGBLike> playerGlowColors = Collections.synchronizedMap(new WeakHashMap<>());
    protected Pos position;
    private double globalRotation;
    private double pitch;
    private final RootBoneEntity<TViewer> rootEntity;

    protected record ModelBoneInfo(String name, Point pivot, Point rotation, JsonArray cubes, float scale) {
    }

    protected final Map<Predicate<String>, Function<ModelBoneInfo, @Nullable ModelBone<TViewer>>> boneSuppliers = new LinkedHashMap<>();
    Function<ModelBoneInfo, ModelBone<TViewer>> defaultBoneSupplier = (info) -> new ModelBonePartDisplay<>(info.pivot, info.name, info.rotation, this, info.scale);

    private final AbstractModelRegistry modelRegistry;
    private final String modelId;

    public AbstractGenericModelImpl(AbstractModelRegistry registry, String modelId) {
        this.modelRegistry = registry;
        this.modelId = modelId;

        registerBoneSuppliers();
        this.rootEntity = getModelPlatform().createRootEntity(this);
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
        this.rootEntity.teleport(position.withView(0, 0));
        this.parts.values().forEach(part -> part.teleport(pos));
    }

    @Override
    public RootBoneEntity<TViewer> getModelRoot() {
        return this.rootEntity;
    }

    public abstract void triggerAnimationEnd(String animation, AnimationHandler.AnimationDirection direction);

    @Override
    public AbstractModelRegistry getModelRegistry() {
        return modelRegistry;
    }

    @Override
    public String getId() {
        return this.modelId;
    }

    protected void init(@NotNull Pos position, float scale) {
        this.position = position;

        JsonObject loadedModel = this.getModelRegistry().getOrLoadGeometry(getId());

        this.setGlobalRotation(position.yaw());

        loadBones(loadedModel, scale);

        for (ModelBone<TViewer> modelBonePart : this.parts.values()) {
            if (modelBonePart instanceof ModelBoneViewable)
                viewableBones.add(modelBonePart);

            BoneEntity<TViewer> entity = modelBonePart.getEntity();
            if(entity != null) {
                //TODO: Not too happy with that spawn method. We might need to rewrite this init stuff
                getModelPlatform().spawn(this, entity, modelBonePart.calculatePosition());
            }

            for (ModelBone<TViewer> child : modelBonePart.getChildren()) {
                BoneEntity<TViewer> childEntity = child.getEntity();
                if(childEntity != null) {
                    getModelPlatform().spawn(this, childEntity, child.calculatePosition());
                }
            }
        }

        getModelPlatform().spawn(this, this.rootEntity, this.position);
        draw();

        this.getParts().stream()
                .map(ModelBone::getEntity)
                .filter(e -> e instanceof ItemDisplayBoneEntity<TViewer> || e instanceof TextDisplayBoneEntity<TViewer>)
                .forEach(rootEntity::attachEntity);

        this.setState("normal");
    }

    @Override
    public void setGlobalScale(float scale) {
        for (ModelBone<TViewer> modelBonePart : this.parts.values()) {
            modelBonePart.setGlobalScale(scale);
        }
    }

    protected void registerBoneSuppliers() {
        boneSuppliers.put(name -> name.equals("nametag") || name.equals("tag_name"), (info) -> new ModelBoneNametag<>(info.pivot(), info.name(), info.rotation(), this, info.scale()));
        boneSuppliers.put(name -> name.contains("hitbox"), (info) -> {
            if (info.cubes.isEmpty()) return null;

            var cube = info.cubes.get(0);
            JsonArray sizeArray = cube.getAsJsonObject().get("size").getAsJsonArray();
            JsonArray p = cube.getAsJsonObject().get("pivot").getAsJsonArray();

            Point sizePoint = new Vec(sizeArray.get(0).getAsFloat(), sizeArray.get(1).getAsFloat(), sizeArray.get(2).getAsFloat());
            Point pivotPoint = new Vec(p.get(0).getAsFloat(), p.get(1).getAsFloat(), p.get(2).getAsFloat());

            var newOffset = pivotPoint.mul(-1, 1, 1);
            return new ModelBoneHitbox<>(info.pivot, info.name, info.rotation, this, newOffset, sizePoint.x(), sizePoint.y(), info.cubes, true, info.scale);
        });
        boneSuppliers.put(name -> name.contains("vfx"), (info) -> new ModelBoneVFX<>(info.pivot, info.name, info.rotation, this, info.scale));
        boneSuppliers.put(name -> name.equals("head"), (info) -> new ModelBoneHeadDisplay<>(info.pivot, info.name, info.rotation, this, info.scale));
    }

    protected void loadBones(JsonObject loadedModel, float scale) {
        // Build bones
        for (JsonElement bone : loadedModel.get("minecraft:geometry").getAsJsonArray().get(0).getAsJsonObject().get("bones").getAsJsonArray()) {
            JsonElement pivot = bone.getAsJsonObject().get("pivot");
            String name = bone.getAsJsonObject().get("name").getAsString();

            Point boneRotation = PositionParser.getPos(bone.getAsJsonObject().get("rotation")).orElse(Pos.ZERO).mul(-1, -1, 1);
            Point pivotPos = PositionParser.getPos(pivot).orElse(Pos.ZERO).mul(-1, 1, 1);

            boolean found = false;
            for (Map.Entry<Predicate<String>, Function<ModelBoneInfo, @Nullable ModelBone<TViewer>>> entry : this.boneSuppliers.entrySet()) {
                var predicate = entry.getKey();
                var supplier = entry.getValue();

                if (predicate.test(name)) {
                    var modelBonePart = supplier.apply(new ModelBoneInfo(name, pivotPos, boneRotation, bone.getAsJsonObject().getAsJsonArray("cubes"), scale));

                    additionalBones.addAll(modelBonePart.getChildren());
                    parts.put(name, modelBonePart);
                    found = true;

                    break;
                }
            }

            if (!found) {
                var modelBonePart = defaultBoneSupplier.apply(new ModelBoneInfo(name, pivotPos, boneRotation, bone.getAsJsonObject().getAsJsonArray("cubes"), scale));

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
                ModelBone<TViewer> child = this.parts.get(name);

                if (child == null) continue;
                ModelBone<TViewer> parentBone = this.parts.get(parentString);

                child.setParent(parentBone);
                parentBone.addChild(child);
            }
        }
    }

    public void setState(String state) {
        for (ModelBone<TViewer> part : viewableBones) {
            part.setState(state);
        }
    }

    public ModelBone<TViewer> getPart(String boneName) {
        return this.parts.get(boneName);
    }

    public void draw() {
        for (ModelBone<TViewer> modelBonePart : this.parts.values()) {
            if (modelBonePart.getParent() == null)
                modelBonePart.draw();
        }
    }

    public void destroy() {
        for (ModelBone<TViewer> modelBonePart : this.parts.values()) {
            modelBonePart.destroy();
        }
        this.rootEntity.remove();

        this.viewableBones.clear();
        this.parts.clear();
    }

    @Override
    public Point getVFX(String name) {
        ModelBone<TViewer> found = this.parts.get(name);
        if (found == null) return null;
        return found.getPosition();
    }

    @Override
    public void setHeadRotation(String name, double rotation) {
        ModelBone<TViewer> found = this.parts.get(name);
        if (found instanceof HeadBone<TViewer> head) head.setRotation(rotation);
    }

    public @NotNull List<ModelBone<TViewer>> getParts() {
        ArrayList<ModelBone<TViewer>> res = this.parts.values().stream()
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
        return modelRegistry.getDiffMapping(getId(), boneName);
    }

    public Point getOffset(String boneName) {
        return modelRegistry.getOffsetMapping(getId(), boneName);
    }

    @Override
    public boolean addViewer(@NotNull TViewer player) {
        getParts().forEach(part -> part.addViewer(player));
        this.rootEntity.addViewer(player);

        var foundTViewerGlowing = this.playerGlowColors.get(player);
        if (foundTViewerGlowing != null)
            this.viewableBones.forEach(part -> part.setGlowing(player, foundTViewerGlowing));

        return this.viewers.add(player);
    }

    @Override
    public boolean removeViewer(@NotNull TViewer player) {
        getParts().forEach(part -> part.removeViewer(player));
        this.rootEntity.removeViewer(player);

        return this.viewers.remove(player);
    }

    @Override
    public @NotNull Set<@NotNull TViewer> getViewers() {
        return Set.copyOf(this.viewers);
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
    public void setGlowing(TViewer player, RGBLike color) {
        this.playerGlowColors.put(player, color);
        this.viewableBones.forEach(part -> part.setGlowing(player, color));
    }

    @Override
    public void removeGlowing(TViewer player) {
        this.playerGlowColors.remove(player);
        this.viewableBones.forEach(part -> part.removeGlowing(player));
    }

    @Override
    public void attachModel(GenericModel<TViewer> model, String boneName) {
        ModelBone<TViewer> bone = this.parts.get(boneName);
        if (bone != null) bone.attachModel(model);
    }

    @Override
    public void detachModel(GenericModel<TViewer> model, String boneName) {
        ModelBone<TViewer> bone = this.parts.get(boneName);
        if (bone != null) bone.detachModel(model);
    }

    @Override
    public Map<String, List<GenericModel<TViewer>>> getAttachedModels() {
        Map<String, List<GenericModel<TViewer>>> attached = new HashMap<>();
        for (ModelBone<TViewer> part : this.parts.values()) {
            attached.put(part.getName(), part.getAttachedModels());
        }
        return attached;
    }

    @Override
    public void setNametag(String name, Component nametag) {
        if (this.parts.get(name) instanceof NametagBone<TViewer> nametagBone) nametagBone.setNametag(nametag);
    }

    @Override
    public Component getNametag(String name) {
        if (this.parts.get(name) instanceof NametagBone<TViewer> nametagBone) return nametagBone.getNametag();
        return null;
    }
}
