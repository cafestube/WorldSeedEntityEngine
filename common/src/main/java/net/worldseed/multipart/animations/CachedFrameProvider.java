package net.worldseed.multipart.animations;

import net.worldseed.multipart.animations.script.PrecomputeScriptExecutor;
import net.worldseed.multipart.animations.script.ScriptExecutor;
import net.worldseed.multipart.blueprint.animation.KeyFrame;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;

import java.util.*;

//TODO: Does full precomputation of frames really make sense? Maybe switch to the computed one.
public class CachedFrameProvider implements FrameProvider {
    private final Map<Short, Point> interpolationCache;
    private final AnimationLoader.AnimationType type;

    public CachedFrameProvider(int length, KeyFrame[] transform, AnimationLoader.AnimationType type) {
        ScriptExecutor executor = new PrecomputeScriptExecutor();
        this.interpolationCache = calculateAllTransforms(executor, length, transform, type);
        this.type = type;
    }

    private Map<Short, Point> calculateAllTransforms(ScriptExecutor executor, double animationTime, KeyFrame[] t, AnimationLoader.AnimationType type) {
        Map<Short, Point> transform = new HashMap<>();
        int ticks = (int) (animationTime * 20);

        for (int i = 0; i <= ticks; i++) {
            var p = calculateTransform(executor, i, t, type);
            transform.put((short) i, p);
        }

        return transform;
    }

    private Point calculateTransform(ScriptExecutor executor, int tick, KeyFrame[] transforms, AnimationLoader.AnimationType type) {
        double toInterpolate = tick * 50.0 / 1000;

        if (type == AnimationLoader.AnimationType.ROTATION) {
            return Interpolation.interpolate(executor, toInterpolate, transforms, Vec.ZERO).mul(RotationMul);
        } else if (type == AnimationLoader.AnimationType.SCALE) {
            return Interpolation.interpolate(executor, toInterpolate, transforms, Vec.ONE);
        } else if (type == AnimationLoader.AnimationType.TRANSLATION) {
            return Interpolation.interpolate(executor, toInterpolate, transforms, Vec.ZERO).mul(TranslationMul)
                    .div(4);
        }

        return Vec.ZERO;
    }

    @Override
    public Point getFrame(ScriptExecutor executor, int tick) {
        return interpolationCache.getOrDefault((short) tick, switch (type) {
            case TRANSLATION, ROTATION -> Vec.ZERO;
            case SCALE -> Vec.ONE;
        });
    }
}
