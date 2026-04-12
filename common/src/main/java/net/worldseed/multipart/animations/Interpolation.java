package net.worldseed.multipart.animations;

import net.worldseed.multipart.animations.script.ScriptExecutor;
import net.worldseed.multipart.blueprint.animation.KeyFrame;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;

import java.util.List;
import java.util.TreeMap;

public enum Interpolation {
    LINEAR("linear") {
        @Override
        public Point interpolate(ScriptExecutor executor, KeyFrame[] keyFrames, int fromIndex, KeyFrame from, int toIndex, KeyFrame to, double time, double timePercent) {
            if(from == to) return to.point().evaluate(executor, time);

            return from.point().evaluate(executor, time).lerp(to.point().evaluate(executor, time), timePercent);
        }
    },

    CATMULL_ROM("catmullrom") {
        @Override
        public Point interpolate(ScriptExecutor executor, KeyFrame[] keyFrames, int fromIndex, KeyFrame fromFrame, int toIndex, KeyFrame toFrame, double time, double timePercent) {
            int beforeFrame = Interpolation.getPreviousKeyframeIndex(keyFrames, fromIndex);
            int afterFrame = Interpolation.getNextKeyframeIndex(keyFrames, toIndex);

            Point before = keyFrames[beforeFrame].point().evaluate(executor, time);
            Point to = toFrame.point().evaluate(executor, time);
            Point from = fromFrame.point().evaluate(executor, time);
            Point after = keyFrames[afterFrame].point().evaluate(executor, time);

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
        public Point interpolate(ScriptExecutor executor, KeyFrame[] keyFrames, int fromIndex, KeyFrame from, int toIndex, KeyFrame to, double time, double timePercent) {
            return from.point().evaluate(executor, time);
        }
    };

    private final String bedrockName;

    Interpolation(String bedrockName) {
        this.bedrockName = bedrockName;
    }

    public abstract Point interpolate(
            ScriptExecutor executor,
            KeyFrame[] keyFrames,
            int fromIndex,
            KeyFrame from,
            int toIndex,
            KeyFrame to,
            double time,
            double timePercent
    );

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

    private static int findPreviousOrCurrentKeyframeIndex(KeyFrame[] keyframes, double currentTime) {
        int left = 0;
        int right = keyframes.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = (left + right) >>> 1;
            double midTime = keyframes[mid].time();

            if (midTime <= currentTime) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    private static int getNextKeyframeIndex(KeyFrame[] keyFrames, int current) {
        if(keyFrames.length <= current+1) return current;
        return current + 1;
    }

    private static int getPreviousKeyframeIndex(KeyFrame[] keyFrames, int current) {
        if(current-1 < 0) return current;
        return current - 1;
    }


    public static Point interpolate(ScriptExecutor executor, double time, KeyFrame[] transform, Vec fallback) {
        if(transform.length == 0) return fallback;
        if(transform.length == 1) return transform[0].point().evaluate(executor, time);

        int index = findPreviousOrCurrentKeyframeIndex(transform, time);
        if(index == -1) return fallback;
        KeyFrame currentOrPrevious = transform[index];
        if(currentOrPrevious.time() == time) { //We are at this exact point, stop here
            return currentOrPrevious.point().evaluate(executor, time);
        }

        int nextFrame = getNextKeyframeIndex(transform, index);
        if(nextFrame == index) { //We have no next frame
            return transform[nextFrame].point().evaluate(executor, time);
        }
        KeyFrame nextKeyFrame = transform[nextFrame];

        double timeDiff = nextKeyFrame.time() - currentOrPrevious.time();
        double timePercent = (time - currentOrPrevious.time()) / timeDiff;

        return currentOrPrevious.interpolation().interpolate(
                executor,
                transform,

                index,
                currentOrPrevious,

                nextFrame,
                nextKeyFrame,

                time,
                timePercent
        );
    }
}
