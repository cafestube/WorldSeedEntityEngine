package net.worldseed.multipart.animations;

import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//TODO: Does full precomputation of frames really make sense? Maybe switch to the computed one.
public class CachedFrameProvider implements FrameProvider {
    private final Map<Short, Point> interpolationCache;
    private final AnimationLoader.AnimationType type;

    public CachedFrameProvider(int length, List<BoneAnimationImpl.KeyFrame> transform, AnimationLoader.AnimationType type) {
        this.interpolationCache = calculateAllTransforms(length, transform, type);
        this.type = type;
    }

    private Map<Short, Point> calculateAllTransforms(double animationTime, List<BoneAnimationImpl.KeyFrame> t, AnimationLoader.AnimationType type) {
        Map<Short, Point> transform = new HashMap<>();
        int ticks = (int) (animationTime * 20);

        for (int i = 0; i <= ticks; i++) {
            var p = calculateTransform(i, t, type, animationTime);
            if (type == AnimationLoader.AnimationType.TRANSLATION) p = p.div(4);
            transform.put((short) i, p);
        }

        return transform;
    }

    private Point calculateTransform(int tick, List<BoneAnimationImpl.KeyFrame> transforms, AnimationLoader.AnimationType type, double length) {
        double toInterpolate = tick * 50.0 / 1000;

        if (type == AnimationLoader.AnimationType.ROTATION) {
            return Interpolation.interpolate(toInterpolate, transforms, length, Vec.ZERO).mul(RotationMul);
        } else if (type == AnimationLoader.AnimationType.SCALE) {
            return Interpolation.interpolate(toInterpolate, transforms, length, Vec.ONE);
        } else if (type == AnimationLoader.AnimationType.TRANSLATION) {
            return Interpolation.interpolate(toInterpolate, transforms, length, Vec.ZERO).mul(TranslationMul);
        }

        return Vec.ZERO;
    }

    @Override
    public Point getFrame(int tick) {
        return interpolationCache.getOrDefault((short) tick, switch (type) {
            case TRANSLATION, ROTATION -> Vec.ZERO;
            case SCALE -> Vec.ONE;
        });
    }
}
