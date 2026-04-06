package net.worldseed.multipart;

import com.google.gson.JsonObject;
import net.worldseed.multipart.blueprint.ModelRenderInformation;
import net.worldseed.multipart.blueprint.animation.AnimationData;
import net.worldseed.multipart.math.Point;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface AbstractModelRegistry {

    Map<String, AnimationData> getOrLoadAnimations(String id);

    JsonObject getOrLoadGeometry(String id);

    @Nullable Point getDiffMapping(String model, String boneName);

    @Nullable Point getOffsetMapping(String model, String boneName);

    @Nullable Map<String, ModelRenderInformation> getModelRenderInfo(String model, String name);

    String getNamespace();
}
