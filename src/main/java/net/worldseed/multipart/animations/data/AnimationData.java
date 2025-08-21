package net.worldseed.multipart.animations.data;

import java.util.Map;

public record AnimationData(double length, Map<String, AnimatedBoneData> bones) {
}
