package net.worldseed.multipart;

import com.google.gson.JsonObject;
import net.worldseed.multipart.animations.data.AnimationData;
import net.worldseed.multipart.math.Point;

import java.util.Map;

public interface AbstractModelRegistry {

    Map<String, AnimationData> getOrLoadAnimations(String id);

    JsonObject getOrLoadGeometry(String id);

    Point getDiffMapping(String model, String boneName);

    Point getOffsetMapping(String model, String boneName);

    String getNamespace();
}
