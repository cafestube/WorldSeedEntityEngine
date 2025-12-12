package net.worldseed.multipart.tracker;

import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.math.Pos;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModelTrackerImpl implements ModelTracker {

    private final Map<PaperModel, TrackedModel> trackedModels = new HashMap<>();
    private final Map<Entity, TrackedModel> entityModelMap = new HashMap<>();

    private BukkitTask trackingTask;

    public ModelTrackerImpl(JavaPlugin plugin) {
        this.trackingTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::updateTracking, 5L, 5L);
    }

    private void updateTracking() {
        for (TrackedModel trackedModel : trackedModels.values()) {

            for (Player viewer : trackedModel.model.getViewers()) {
                if(!viewer.isOnline() || !viewer.getWorld().equals(trackedModel.model.getWorld()) || distanceSquared(trackedModel.model.getPosition(), viewer.getLocation()) > 120*120) {
                    trackedModel.model.removeViewer(viewer);
                }
            }

            for (Player player : trackedModel.model.getWorld().getPlayers()) {
                if(!trackedModel.model.getViewers().contains(player)
                        && trackedModel.rule.shouldTrack(trackedModel.model, player)
                        && distanceSquared(trackedModel.model.getPosition(), player.getLocation()) <= 120*120) {
                    trackedModel.model.addViewer(player);
                }
            }

        }
    }

    private double distanceSquared(Pos position, @NotNull Location location) {
        return NumberConversions.square(position.x() - location.getX()) + NumberConversions.square(position.y() - location.getY())
                + NumberConversions.square(position.z() - location.getZ());
    }

    public void destroy() {
        if(trackingTask != null) {
            trackingTask.cancel();
            trackingTask = null;
        }
        for(PaperModel model : new ArrayList<>(trackedModels.keySet())) {
            stopTracking(model);
        }
        trackedModels.clear();
        entityModelMap.clear();
    }

    @Override
    public void startTracking(PaperModel model, TrackingRule rule) {
        if(trackedModels.containsKey(model))
            throw new IllegalStateException("Model is already being tracked");

        model.setModelTracker(this);
        model.setBoundEntity(null);
        this.trackedModels.put(model, new TrackedModel(model, null, rule));


    }

    @Override
    public void startTrackingOn(PaperModel model, Entity entity, TrackingRule rule) {
        throw new UnsupportedOperationException("Entity tracking is not yet supported in this implementation");
        //TODO:
    }

    @Override
    public void stopTracking(PaperModel model) {
        TrackedModel trackedModel = trackedModels.remove(model);
        if(trackedModel != null && trackedModel.entity != null) {
            entityModelMap.remove(trackedModel.entity);
            //TODO:
        }
        model.setModelTracker(null);
        model.setBoundEntity(null);

        for (Player viewer : model.getViewers()) {
            model.removeViewer(viewer);
        }
    }

    @Override
    public PaperModel getTrackedModel(Entity entity) {
        TrackedModel value = entityModelMap.get(entity);
        if(value == null) return null;
        return value.model;
    }

    @Override
    public Collection<PaperModel> getAllTrackedModels() {
        return trackedModels.keySet();
    }

    record TrackedModel(PaperModel model, Entity entity, TrackingRule rule) {}
}
