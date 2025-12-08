package net.worldseed.multipart.animations;

import com.google.gson.*;
import net.worldseed.multipart.animations.data.AnimatedBoneData;
import net.worldseed.multipart.animations.data.AnimationData;
import net.worldseed.multipart.animations.data.BoneAnimationData;
import net.worldseed.multipart.math.PositionParser;
import net.worldseed.multipart.mql.MQLPoint;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class AnimationLoader {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();


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

        final int convertedLength = (int) (length * 20);

        for (Map.Entry<String, JsonElement> boneEntry : animation.getAsJsonObject().get("bones").getAsJsonObject().entrySet()) {
            String boneName = boneEntry.getKey();

            JsonElement animationRotation = boneEntry.getValue().getAsJsonObject().get("rotation");
            JsonElement animationPosition = boneEntry.getValue().getAsJsonObject().get("position");
            JsonElement animationScale = boneEntry.getValue().getAsJsonObject().get("scale");

            BoneAnimationData rotation = null;
            BoneAnimationData position = null;
            BoneAnimationData scale = null;

            if (length != 0) {
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
                    rotation = new BoneAnimationData(computeMathTransforms(convertedLength, animationRotation, AnimationType.ROTATION));
                }
                if (animationPosition != null) {
                    position = new BoneAnimationData(computeMathTransforms(convertedLength, animationPosition, AnimationType.TRANSLATION));
                }
                if (animationScale != null) {
                    scale = new BoneAnimationData(computeMathTransforms(convertedLength, animationScale, AnimationType.SCALE));
                }
            }

            bones.put(boneName, new AnimatedBoneData(rotation, position, scale));
        }

        return new AnimationData(length, bones);
    }

    private static FrameProvider computeMathTransforms(int length, JsonElement keyframes, AnimationType type) {
        List<BoneAnimationImpl.KeyFrame> transform = parseKeyFrames(keyframes);
        return new ComputedFrameProvider(transform, type, length);
    }

    private static FrameProvider computeCachedTransforms(int length, JsonElement keyframes, AnimationType type) {
        List<BoneAnimationImpl.KeyFrame> transform = parseKeyFrames(keyframes);
        return new CachedFrameProvider(length, transform, type);
    }

    private static List<BoneAnimationImpl.KeyFrame> parseKeyFrames(JsonElement keyframes) {
        JsonObject keyFrameObj = keyframes.getAsJsonObject();
        List<BoneAnimationImpl.KeyFrame> transform = new ArrayList<>(keyFrameObj.size());

        try {
            for (Map.Entry<String, JsonElement> entry : keyFrameObj.entrySet()) {
                double time = Double.parseDouble(entry.getKey());

                if (entry.getValue() instanceof JsonObject obj) {
                    Interpolation lerp = Interpolation.fromBedrockName(entry.getValue().getAsJsonObject().get("lerp_mode").getAsString());

                    if (obj.get("post") instanceof JsonArray arr) {
                        if (arr.get(0) instanceof JsonObject) {
                            MQLPoint point = PositionParser.getMQLPos(obj.get("post").getAsJsonArray().get(0)).orElse(MQLPoint.ZERO);
                            transform.add(new BoneAnimationImpl.KeyFrame(time, point, lerp));
                        } else {
                            MQLPoint point = PositionParser.getMQLPos(obj.get("post").getAsJsonArray()).orElse(MQLPoint.ZERO);
                            transform.add(new BoneAnimationImpl.KeyFrame(time, point, lerp));
                        }
                    }
                } else if (entry.getValue() instanceof JsonArray arr) {
                    MQLPoint point = PositionParser.getMQLPos(arr).orElse(MQLPoint.ZERO);
                    transform.add(new BoneAnimationImpl.KeyFrame(time, point, Interpolation.LINEAR));
                }
            }
        } catch (IllegalStateException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException | IllegalAccessException e) {

            try {
                e.printStackTrace();
                MQLPoint point = PositionParser.getMQLPos(keyFrameObj).orElse(MQLPoint.ZERO);
                transform.add(new BoneAnimationImpl.KeyFrame(0.0, point, Interpolation.LINEAR));
            } catch (Exception e2) {
                e.printStackTrace();
            }

        }

        Collections.sort(transform);
        return transform;
    }


    public enum AnimationType {
        ROTATION, SCALE, TRANSLATION
    }
}
