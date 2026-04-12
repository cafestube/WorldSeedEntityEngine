package net.worldseed.multipart.blueprint.animation;

public record AnimatedBoneData(
        BoneAnimationData rotation,
        BoneAnimationData position,
        BoneAnimationData scale,
        boolean rotateInGlobalSpace
) { }
