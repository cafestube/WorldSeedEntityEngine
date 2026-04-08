package net.worldseed.multipart.blueprint.animation;

import net.worldseed.multipart.animations.Interpolation;
import net.worldseed.multipart.mql.MQLPoint;
import org.jetbrains.annotations.NotNull;

public record KeyFrame(double time, MQLPoint p, Interpolation lerp) implements Comparable<KeyFrame> {

    @Override
    public int compareTo(@NotNull KeyFrame o) {
        return Double.compare(time, o.time);
    }

}