package net.worldseed.multipart.tracker;

import net.worldseed.multipart.PaperModel;
import org.bukkit.entity.Player;

public interface TrackingRule {

    boolean shouldTrack(PaperModel model, Player viewer);

    static TrackingRule always() {
        return (model, viewer) -> true;
    }
}
