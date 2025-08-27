package net.worldseed.multipart;

import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.events.AnimationCompleteEvent;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.model_bones.PaperRootBoneEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GenericModelImpl extends AbstractGenericModelImpl<Player> implements PaperModel {

    protected World world;

    public GenericModelImpl(ModelRegistry registry, String modelId) {
        super(registry, modelId);
    }

    @Override
    public ModelRegistry getModelRegistry() {
        return (ModelRegistry) super.getModelRegistry();
    }

    @Override
    public PaperRootBoneEntity getModelRoot() {
        return (PaperRootBoneEntity) super.getModelRoot();
    }

    public void setPosition(Location position) {
        setPosition(PositionConversion.fromPaper(position));
    }

    public void triggerAnimationEnd(String animation, AnimationHandler.AnimationDirection direction) {
        new AnimationCompleteEvent(this, animation, direction).callEvent();
    }

    public void init(@Nullable World instance, @NotNull Pos position) {
        init(instance, position, 1);
    }

    @Override
    public ModelPlatform<Player> getModelPlatform() {
        return PaperModelPlatform.INSTANCE;
    }


    public void init(Location pos) {
        init(pos.getWorld(), PositionConversion.fromPaper(pos));
    }

    public void init(Location location, float scale) {
        init(location.getWorld(), PositionConversion.fromPaper(location), scale);
    }

    public void init(@Nullable World instance, @NotNull Pos position, float scale) {
        this.world = instance;
        super.init(position, scale);
    }

    protected void registerBoneSuppliers() {
        super.registerBoneSuppliers();
//        boneSuppliers.put(name -> name.equals("nametag") || name.equals("tag_name"), (info) -> new ModelBoneNametag(info.pivot(), info.name(), info.rotation(), this, info.scale()));
//        boneSuppliers.put(name -> name.contains("hitbox"), (info) -> {
//            if (info.cubes().isEmpty()) return null;
//
//            var cube = info.cubes().get(0);
//            JsonArray sizeArray = cube.getAsJsonObject().get("size").getAsJsonArray();
//            JsonArray p = cube.getAsJsonObject().get("pivot").getAsJsonArray();
//
//            Point sizePoint = new Vec(sizeArray.get(0).getAsFloat(), sizeArray.get(1).getAsFloat(), sizeArray.get(2).getAsFloat());
//            Point pivotPoint = new Vec(p.get(0).getAsFloat(), p.get(1).getAsFloat(), p.get(2).getAsFloat());
//
//            var newOffset = pivotPoint.mul(-1, 1, 1);
//            return new ModelBoneHitbox(info.pivot(), info.name(), info.rotation(), this, newOffset, sizePoint.x(), sizePoint.y(), info.cubes(), true, info.scale());
//        });
//        boneSuppliers.put(name -> name.contains("seat"), (info) -> new ModelBoneSeat(info.pivot(), info.name(), info.rotation(), this, info.scale()));
    }

    public World getWorld() {
        return world;
    }

}
