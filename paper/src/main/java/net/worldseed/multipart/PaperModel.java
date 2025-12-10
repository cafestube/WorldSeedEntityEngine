package net.worldseed.multipart;

import net.worldseed.multipart.entity.PaperRootBoneEntity;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.tracker.ModelTracker;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface PaperModel extends GenericModel<Player> {

    static PaperModel model(ModelRegistry registry, String modelId, JavaPlugin plugin) {
        return new GenericModelImpl(registry, modelId, plugin);
    }

    static PaperModel model(ModelEngine engine, String modelId) {
        return new GenericModelImpl(engine.getModelRegistry(), modelId, engine.getPlugin());
    }

    void init(@NotNull Location position);

    void init(@org.jetbrains.annotations.Nullable World instance, @NotNull Pos position);

    void setPosition(@NotNull Location position);

    void setPosition(@NotNull Pos position);

    @Override
    ModelRegistry getModelRegistry();

//    void mountEntity(String name, Entity entity);
//
//    void dismountEntity(String name, Entity entity);
//
//    Collection<Entity> getPassengers(String name);

    World getWorld();

    JavaPlugin getPlugin();

    @Override
    PaperRootBoneEntity getModelRoot();

    @Nullable
    ModelTracker getModelTracker();

    @ApiStatus.OverrideOnly
    void setModelTracker(@Nullable ModelTracker modelTracker);

    @Nullable
    Entity getBoundEntity();

    @ApiStatus.OverrideOnly
    void setBoundEntity(@Nullable Entity entity);
}
