package net.worldseed.multipart.animations;

import com.google.gson.*;
import net.worldseed.multipart.blueprint.animation.AnimatedBoneData;
import net.worldseed.multipart.blueprint.animation.AnimationData;
import net.worldseed.multipart.blueprint.animation.BoneAnimationData;
import net.worldseed.multipart.blueprint.animation.KeyFrame;
import net.worldseed.multipart.math.PositionParser;
import net.worldseed.multipart.mql.MQLPoint;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class AnimationLoader {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    //TODO: Maybe system property?
    //TODO: Check how we continue with this. Cached doesn't give that much of a performance boost
    public static final boolean USE_CACHED_TRANSFORM = true;

    public static Map<String, AnimationData> parseAnimations(String animationString) {
        JsonObject animations = GSON.fromJson(new StringReader(animationString), JsonObject.class);
        return parseAnimations(animations);
    }

    public static Map<String, AnimationData> parseAnimations(JsonObject animations) {
        Map<String, AnimationData> res = new LinkedHashMap<>();

        for (Map.Entry<String, JsonElement> animation : animations.get("animations").getAsJsonObject().entrySet()) {
            res.put(animation.getKey(), parseAnimation(animation.getValue().getAsJsonObject()));
        }

        return res;
    }


    public static AnimationData parseAnimation(JsonObject animation) {
        final JsonElement animationLength = animation.get("animation_length");
        final double length = animationLength == null ? 0 : animationLength.getAsDouble();
        final Map<String, AnimatedBoneData> bones = new HashMap<>();
        final boolean loop = animation.has("loop") && animation.get("loop").getAsBoolean();
        final boolean override = animation.has("override_previous_animation") && animation.get("override_previous_animation").getAsBoolean();

        final int convertedLength = (int) (length * 20);

        for (Map.Entry<String, JsonElement> boneEntry : animation.getAsJsonObject().get("bones").getAsJsonObject().entrySet()) {
            String boneName = boneEntry.getKey();
            JsonObject boneObject = boneEntry.getValue().getAsJsonObject();

            JsonElement animationRotation = boneObject.get("rotation");
            JsonElement animationPosition = boneObject.get("position");
            JsonElement animationScale = boneObject.get("scale");
            boolean rotateInGlobalSpace = shouldRotateInGlobalSpace(boneObject);

            BoneAnimationData rotation = null;
            BoneAnimationData position = null;
            BoneAnimationData scale = null;

            if (length != 0 && USE_CACHED_TRANSFORM) {
                if (animationRotation != null) {
                    rotation = new BoneAnimationData(computeCachedTransforms(convertedLength, animationRotation, AnimationType.ROTATION));
                }
                if (animationPosition != null) {
                    position = new BoneAnimationData(computeCachedTransforms(convertedLength, animationPosition, AnimationType.TRANSLATION));
                }
                if (animationScale != null) {
                    scale = new BoneAnimationData(computeCachedTransforms(convertedLength, animationScale, AnimationType.SCALE));
                }
            } else {
                if (animationRotation != null) {
                    rotation = new BoneAnimationData(computeMathTransforms(animationRotation, AnimationType.ROTATION));
                }
                if (animationPosition != null) {
                    position = new BoneAnimationData(computeMathTransforms(animationPosition, AnimationType.TRANSLATION));
                }
                if (animationScale != null) {
                    scale = new BoneAnimationData(computeMathTransforms(animationScale, AnimationType.SCALE));
                }
            }

            bones.put(boneName, new AnimatedBoneData(rotation, position, scale, rotateInGlobalSpace));
        }

        return new AnimationData(loop, override, length, bones);
    }

    private static FrameProvider computeMathTransforms(JsonElement keyframes, AnimationType type) {
        KeyFrame[] transform = parseKeyFrames(keyframes);
        return new ComputedFrameProvider(transform, type);
    }

    private static boolean shouldRotateInGlobalSpace(JsonObject boneObject) {
        JsonElement relativeToElement = boneObject.get("relative_to");
        if (!(relativeToElement instanceof JsonObject relativeTo)) {
            return false;
        }

        JsonElement rotationElement = relativeTo.get("rotation");
        if (rotationElement == null || !rotationElement.isJsonPrimitive()) {
            return false;
        }

        return "entity".equalsIgnoreCase(rotationElement.getAsString());
    }

    private static FrameProvider computeCachedTransforms(int length, JsonElement keyframes, AnimationType type) {
        KeyFrame[] transform = parseKeyFrames(keyframes);
        return new CachedFrameProvider(length, transform, type);
    }

    private static KeyFrame[] parseKeyFrames(JsonElement keyframes) {
        JsonObject keyFrameObj = keyframes.getAsJsonObject();
        List<KeyFrame> transform = new ArrayList<>(keyFrameObj.size());

        try {
            for (Map.Entry<String, JsonElement> entry : keyFrameObj.entrySet()) {
                double time = Double.parseDouble(entry.getKey());

                if (entry.getValue() instanceof JsonObject obj) {
                    Interpolation lerp = Interpolation.fromBedrockName(entry.getValue().getAsJsonObject().get("lerp_mode").getAsString());

                    if (obj.get("post") instanceof JsonArray arr) {
                        if (arr.get(0) instanceof JsonObject) {
                            MQLPoint point = PositionParser.getMQLPos(obj.get("post").getAsJsonArray().get(0)).orElse(MQLPoint.ZERO);
                            transform.add(new KeyFrame(time, point, lerp));
                        } else {
                            MQLPoint point = PositionParser.getMQLPos(obj.get("post").getAsJsonArray()).orElse(MQLPoint.ZERO);
                            transform.add(new KeyFrame(time, point, lerp));
                        }
                    }
                } else if (entry.getValue() instanceof JsonArray arr) {
                    MQLPoint point = PositionParser.getMQLPos(arr).orElse(MQLPoint.ZERO);
                    transform.add(new KeyFrame(time, point, Interpolation.LINEAR));
                }
            }
        } catch (IllegalStateException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException | IllegalAccessException e) {

            try {
                e.printStackTrace();
                MQLPoint point = PositionParser.getMQLPos(keyFrameObj).orElse(MQLPoint.ZERO);
                transform.add(new KeyFrame(0.0, point, Interpolation.LINEAR));
            } catch (Exception e2) {
                e.printStackTrace();
            }

        }

        Collections.sort(transform);
        return transform.toArray(new KeyFrame[0]);
    }


    public enum AnimationType {
        ROTATION, SCALE, TRANSLATION
    }
}
