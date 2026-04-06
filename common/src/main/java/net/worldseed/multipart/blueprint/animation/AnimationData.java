package net.worldseed.multipart.blueprint.animation;

import java.util.Map;

public record AnimationData(double length, Map<String, AnimatedBoneData> bones) {
}
