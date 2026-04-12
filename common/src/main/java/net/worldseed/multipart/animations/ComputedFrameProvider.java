package net.worldseed.multipart.animations;

import net.worldseed.multipart.animations.script.ScriptExecutor;
import net.worldseed.multipart.blueprint.animation.KeyFrame;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;

import java.util.List;
import java.util.TreeMap;

public class ComputedFrameProvider implements FrameProvider {
    private final KeyFrame[] transform;
    private final AnimationLoader.AnimationType type;

    public ComputedFrameProvider(KeyFrame[] transform, AnimationLoader.AnimationType type) {
        this.transform = transform;
        this.type = type;

    }

    @Override
    public Point getFrame(ScriptExecutor instance, int tick) {
        if (transform.length == 0) return getFallbackPoint();

        double toInterpolate = tick * 50.0 / 1000;

        Point point = Interpolation.interpolate(
                instance,
                toInterpolate,
                transform,
                getFallbackPoint()
        );

        if (type == AnimationLoader.AnimationType.ROTATION) return point.mul(RotationMul);
        if (type == AnimationLoader.AnimationType.TRANSLATION) return point.mul(TranslationMul).mul(0.25);
        return point;
    }

    private Vec getFallbackPoint() {
        return switch (type) {
            case TRANSLATION, ROTATION -> Vec.ZERO;
            case SCALE -> Vec.ONE;
        };
    }
}
