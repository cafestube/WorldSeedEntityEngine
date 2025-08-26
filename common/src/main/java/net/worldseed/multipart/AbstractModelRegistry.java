package net.worldseed.multipart;

import net.worldseed.multipart.animations.data.AnimationData;

import java.util.Map;

public interface AbstractModelRegistry {

    Map<String, AnimationData> getOrLoadAnimations(String id);

}
