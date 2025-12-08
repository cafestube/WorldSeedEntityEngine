package net.worldseed.multipart.animations;

import net.worldseed.multipart.animations.BoneAnimationImpl.KeyFrame;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;

import java.security.Key;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public enum Interpolation {
    LINEAR("linear") {
        @Override
        public Point interpolate(List<KeyFrame> points, int toIndex, double time, double timePercent) {
            KeyFrame to = points.get(toIndex);
            KeyFrame from = toIndex > 0 ? points.get(toIndex - 1) : to;
            if(to == from) return to.p().evaluate(time);

            return from.p().evaluate(time).lerp(to.p().evaluate(time), timePercent);
        }
    },

    CATMULL_ROM("catmullrom") {
        @Override
        public Point interpolate(List<KeyFrame> points, int toIndex, double time, double timePercent) {
            Point before = (toIndex > 1 ? points.get(toIndex - 2) : points.getFirst()).p().evaluate(time);
            KeyFrame toFrame = points.get(toIndex);
            Point to = toFrame.p().evaluate(time);
            Point from = (toIndex > 0 ? points.get(toIndex - 1) : toFrame).p().evaluate(time);
            Point after = ( toIndex + 1 < points.size() ? points.get(toIndex + 1) : points.getLast()).p().evaluate(time);

            return new Vec(
                    catmullRom(before.x(), from.x(), to.x(), after.x(), timePercent),
                    catmullRom(before.y(), from.y(), to.y(), after.y(), timePercent),
                    catmullRom(before.z(), from.z(), to.z(), after.z(), timePercent)
            );
        }

        private double catmullRom(double p0, double p1, double p2, double p3, double t) {
            return 0.5 * ((2 * p1) + (-p0 + p2) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t + (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t);
        }
    },

    //BEZIER("bezier"), //TODO: Figure out how that would work

    STEP("step") {
        @Override
        public Point interpolate(List<KeyFrame> points, int toIndex, double time, double timePercent) {
            return (toIndex > 0 ? points.get(toIndex - 1) : points.get(toIndex)).p().evaluate(time);
        }
    };

    private final String bedrockName;

    Interpolation(String bedrockName) {
        this.bedrockName = bedrockName;
    }

    public abstract Point interpolate(List<KeyFrame> keyFrames, int toIndex, double time, double timePercent);

    public static Interpolation fromBedrockName(String name) {
        if(name == null) {
            return LINEAR; // Default to LINEAR if name is null
        }

        for (Interpolation interpolation : values()) {
            if (interpolation.bedrockName.equalsIgnoreCase(name)) {
                return interpolation;
            }
        }
        return LINEAR; // Default to LINEAR if not found
    }

    public static Point interpolate(double time, List<BoneAnimationImpl.KeyFrame> transform, double animationTime, Vec fallback) {
        if(transform.isEmpty()) return fallback;

        int currentIndex = 0;
        for (int i = 0; i < transform.size(); i++) {
            if (transform.get(i).time() > time) {
                break;
            }
            currentIndex = i;
        }

        currentIndex = Math.min(currentIndex, transform.size() - 1);
        int nextIndex = Math.min(currentIndex+1, transform.size() - 1);

        BoneAnimationImpl.KeyFrame currentFrame = transform.get(currentIndex);
        BoneAnimationImpl.KeyFrame nextFrame = transform.get(nextIndex);

        if(currentFrame == nextFrame) {
            return currentFrame.p().evaluate(time);
        }

        double timeDiff = nextFrame.time() - currentFrame.time();
        double timePercent = (time - currentFrame.time()) / timeDiff;

        return nextFrame.lerp().interpolate(transform, nextIndex, time, timePercent);
    }
}
