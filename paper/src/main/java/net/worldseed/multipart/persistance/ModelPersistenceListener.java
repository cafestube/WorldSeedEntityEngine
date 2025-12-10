
package net.worldseed.multipart.persistance;

import net.worldseed.multipart.ModelEngine;
import net.worldseed.multipart.ModelRegistry;
import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.tracker.TrackingRule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

//Inspired by hephaestus engine
public class ModelPersistenceListener implements Listener {

    private final ModelEngine modelEngine;

    public ModelPersistenceListener(ModelEngine modelRegistry) {
        this.modelEngine = modelRegistry;

        fixupLoadedEntities();
    }

    private void fixupLoadedEntities() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                PaperModel trackedModel = modelEngine.getModelTracker().getTrackedModel(entity);

                if (trackedModel != null) {
                    // Already has a model
                    continue;
                }

                loadEntity(entity);
            }
        }
    }

    private void loadEntity(Entity entity) {
        modelEngine.getModelPersistenceHandler().determineModel(entity).whenComplete((model, err) -> {
            if(err != null) {
                modelEngine.getPlugin().getLogger().log(Level.SEVERE, "Error while determining model for entity " + entity + ": " + err.getMessage(), err);
                return;
            }

            if(model == null || !entity.isValid()) return;
            modelEngine.getModelTracker().startTrackingOn(model, entity, TrackingRule.always());
        });
    }

    @EventHandler
    public void onEntitiesLoad(final @NotNull EntitiesLoadEvent event) {
        for (final var entity : event.getEntities()) {
            loadEntity(entity);
        }
    }

    @EventHandler
    public void onEntitiesUnload(final @NotNull EntitiesUnloadEvent event) {
        for (final var entity : event.getEntities()) {
            final PaperModel view = modelEngine.getModelTracker().getTrackedModel(entity);
            if (view != null) {
                modelEngine.getModelPersistenceHandler().saveModel(entity, view);
            }
        }
    }

    public void onPluginDisable() { // Called by ModelEngine#close()
        for (final var world : Bukkit.getWorlds()) {
            for (final var entity : world.getEntities()) {
                final PaperModel view = modelEngine.getModelTracker().getTrackedModel(entity);
                if (view != null) {
                    modelEngine.getModelPersistenceHandler().saveModel(entity, view);
                }
            }
        }
    }


}
