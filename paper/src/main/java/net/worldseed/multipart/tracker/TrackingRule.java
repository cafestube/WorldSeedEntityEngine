package net.worldseed.multipart.tracker;

import net.worldseed.multipart.PaperModel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface TrackingRule {

    /**
     * Determines whether the given viewer should track
     * the given model
     *
     * @param model The model to check
     * @param viewer The viewer candidate, can be null when referring to global visibility (e.g. server replays)
     */
    boolean shouldTrack(PaperModel model, @Nullable Player viewer);

    static TrackingRule always() {
        return (model, viewer) -> true;
    }
}
