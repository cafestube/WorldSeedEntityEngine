package net.worldseed.multipart.blueprint.animation;

import java.util.Map;

public record AnimationData(boolean loop, boolean overrideBones, double length, Map<String, AnimatedBoneData> bones) {
}
