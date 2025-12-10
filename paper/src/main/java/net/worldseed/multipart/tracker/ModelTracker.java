package net.worldseed.multipart.tracker;

import net.worldseed.multipart.PaperModel;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.List;

public interface ModelTracker {

    void startTracking(PaperModel model, TrackingRule rule);

    /**
     * Starts tracking the given model on the specified entity.
     * This means that the model will be synced with the entity's position and sent to the client
     * when the host entity is in range. Default animations (walk, fly, idle) will also be handled automatically.
     *
     * @param model The model to track.
     * @param entity The entity to track the model on.
     * @param rule The tracking rule
     */
    void startTrackingOn(PaperModel model, Entity entity, TrackingRule rule);

    void stopTracking(PaperModel model);

    PaperModel getTrackedModel(Entity entity);

    Collection<PaperModel> getAllTrackedModels();

}
