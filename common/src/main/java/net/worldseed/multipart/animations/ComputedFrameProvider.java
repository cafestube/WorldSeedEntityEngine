package net.worldseed.multipart.animations;

import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;

import java.util.LinkedHashMap;
import java.util.List;

public class ComputedFrameProvider implements FrameProvider {
    private final List<BoneAnimationImpl.KeyFrame> transform;
    private final AnimationLoader.AnimationType type;

    public ComputedFrameProvider(List<BoneAnimationImpl.KeyFrame> transform, AnimationLoader.AnimationType type, int length) {
        this.transform = transform;
        this.type = type;
    }

    @Override
    public Point getFrame(int tick) {
        var first = transform.getFirst();
        if (first == null) return switch (type) {
            case TRANSLATION, ROTATION -> Vec.ZERO;
            case SCALE -> Vec.ONE;
        };

        double toInterpolate = tick * 50.0 / 1000;
        var point = first.p().evaluate(toInterpolate);

        //TODO: Interpolation

        if (type == AnimationLoader.AnimationType.ROTATION) return point.mul(RotationMul);
        if (type == AnimationLoader.AnimationType.TRANSLATION) return point.mul(TranslationMul).mul(0.25);
        return point;
    }
}
