package net.worldseed.multipart;

import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.events.AnimationCompleteEvent;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.entity.PaperRootBoneEntity;
import net.worldseed.multipart.tracker.ModelTracker;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GenericModelImpl extends AbstractGenericModelImpl<Player> implements PaperModel {

    protected World world;
    protected JavaPlugin plugin;
    protected @Nullable ModelTracker modelTracker;
    protected @Nullable Entity boundEntity;

    public GenericModelImpl(ModelRegistry registry, String modelId, JavaPlugin plugin) {
        super(registry, modelId);
        this.plugin = plugin;
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
//        boneSuppliers.put(name -> name.contains("seat"), (info) -> new ModelBoneSeat(info.pivot(), info.name(), info.rotation(), this, info.scale()));
    }

    @Override
    public boolean isSpawned() {
        return getWorld() != null;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public @Nullable ModelTracker getModelTracker() {
        return this.modelTracker;
    }

    public void setModelTracker(@Nullable ModelTracker modelTracker) {
        this.modelTracker = modelTracker;
    }

    @Override
    public @Nullable Entity getBoundEntity() {
        return this.boundEntity;
    }

    public void bindToEntity(@Nullable Entity entity) {
        this.boundEntity = entity;
    }

}
