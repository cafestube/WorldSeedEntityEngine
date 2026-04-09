package net.worldseed.multipart.animations;

import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;

public record BoneAnimationTransform(
        Point translation,
        Point rotation,
        Point scale
) {

    public static BoneAnimationTransform ZERO = new BoneAnimationTransform(
            Vec.ZERO,
            Vec.ZERO,
            Vec.ONE
    );

}
