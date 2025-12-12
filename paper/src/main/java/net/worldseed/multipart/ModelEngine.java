package net.worldseed.multipart;

import net.worldseed.multipart.persistance.ModelPersistenceHandler;
import net.worldseed.multipart.persistance.ModelPersistenceListener;
import net.worldseed.multipart.tracker.ModelTracker;
import net.worldseed.multipart.tracker.ModelTrackerImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class ModelEngine {

    private final JavaPlugin plugin;
    private final ModelPersistenceHandler modelPersistenceHandler;
    private final ModelPlatform<Player> modelPlatform = PaperModelPlatform.INSTANCE;
    private final ModelTrackerImpl modelTracker;

    private final ModelPersistenceListener persistenceListener;

    public ModelEngine(JavaPlugin plugin, ModelPersistenceHandler modelPersistenceHandler) {
        this.plugin = plugin;
        this.modelPersistenceHandler = modelPersistenceHandler;
        this.modelTracker = new ModelTrackerImpl(plugin);

        if(modelPersistenceHandler != null) {
            this.persistenceListener = new ModelPersistenceListener(this);
            plugin.getServer().getPluginManager().registerEvents(persistenceListener, plugin);
        } else {
            this.persistenceListener = null;
        }
    }

    public void close() {
        persistenceListener.onPluginDisable();
        HandlerList.unregisterAll(this.persistenceListener);
        this.modelTracker.destroy();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public ModelPersistenceHandler getModelPersistenceHandler() {
        return modelPersistenceHandler;
    }

    public ModelPlatform<Player> getModelPlatform() {
        return modelPlatform;
    }

    public ModelTracker getModelTracker() {
        return modelTracker;
    }
}
