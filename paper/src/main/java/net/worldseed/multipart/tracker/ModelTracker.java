package net.worldseed.multipart.tracker;

import net.worldseed.multipart.PaperModel;
import org.bukkit.entity.Entity;

public interface ModelTracker {

    void startTracking(PaperModel model, TrackingRule rule);

    void startTracking(PaperModel model, Entity entity, TrackingRule rule);

    void stopTracking(PaperModel model);

    PaperModel getTrackedModel(Entity entity);

}
