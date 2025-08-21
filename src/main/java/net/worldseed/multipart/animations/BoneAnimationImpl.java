package net.worldseed.multipart.animations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.worldseed.multipart.ModelEngine;
import net.worldseed.multipart.animations.data.BoneAnimationData;
import net.worldseed.multipart.model_bones.ModelBone;
import net.worldseed.multipart.mql.MQLPoint;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class BoneAnimationImpl implements BoneAnimation {
    private final AnimationLoader.AnimationType type;

    private final FrameProvider frameProvider;
    private final int length;
    private final String name;
    private final String boneName;
    private boolean playing = false;
    private short tick = 0;
    private AnimationHandlerImpl.AnimationDirection direction = AnimationHandlerImpl.AnimationDirection.FORWARD;

    BoneAnimationImpl(String animationName, String boneName, ModelBone bone, BoneAnimationData keyframes, AnimationLoader.AnimationType animationType, double length) {
        this.type = animationType;
        this.length = (int) (length * 20);
        this.name = animationName;
        this.boneName = boneName;

        this.frameProvider = keyframes.frameProvider();
        bone.addAnimation(this);
    }

    public AnimationLoader.AnimationType getType() {
        return type;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void tick() {
        if (playing) {
            if (direction == AnimationHandlerImpl.AnimationDirection.FORWARD) {
                tick++;
                if (tick > length && length != 0) tick = 0;
            } else if (direction == AnimationHandlerImpl.AnimationDirection.BACKWARD) {
                tick--;
                if (tick < 0 && length != 0) tick = (short) length;
            }
        }
    }

    public Point getTransform() {
        if (!this.playing) return switch (this.type) {
            case ROTATION, TRANSLATION -> Vec.ZERO;
            case SCALE -> Vec.ONE;
        };
        return this.frameProvider.getFrame(tick);
    }

    public Point getTransformAtTime(int time) {
        return this.frameProvider.getFrame(time);
    }

    public void setDirection(AnimationHandlerImpl.AnimationDirection direction) {
        this.direction = direction;
    }

    private FrameProvider computeMathTransforms(JsonElement keyframes) {
        LinkedHashMap<Double, PointInterpolation> transform = new LinkedHashMap<>();

        try {
            for (Map.Entry<String, JsonElement> entry : keyframes.getAsJsonObject().entrySet()) {
                double time = Double.parseDouble(entry.getKey());
                MQLPoint point = ModelEngine.getMQLPos(entry.getValue().getAsJsonObject().get("post").getAsJsonArray().get(0).getAsJsonObject()).orElse(MQLPoint.ZERO);
                String lerp = entry.getValue().getAsJsonObject().get("lerp_mode").getAsString();
                transform.put(time, new PointInterpolation(point, lerp));
            }
        } catch (IllegalStateException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException | IllegalAccessException e) {
            try {
                e.printStackTrace();
                MQLPoint point = ModelEngine.getMQLPos(keyframes.getAsJsonObject()).orElse(MQLPoint.ZERO);
                transform.put(0.0, new PointInterpolation(point, "linear"));
            } catch (Exception e2) {
                e.printStackTrace();
            }
        }

        return new ComputedFrameProvider(transform, type, length);
    }



    public void stop() {
        this.tick = 0;
        this.playing = false;
        this.direction = AnimationHandler.AnimationDirection.FORWARD;
    }

    public void play() {
        if (this.direction == AnimationHandler.AnimationDirection.FORWARD) this.tick = 0;
        else if (this.direction == AnimationHandler.AnimationDirection.BACKWARD) this.tick = (short) (length - 1);
        this.playing = true;
    }

    public void resume(short tick) {
        this.tick = tick;
        this.playing = true;
    }

    public String name() {
        return name;
    }

    public String boneName() {
        return boneName;
    }

    public short getTick() {
        return tick;
    }

    record PointInterpolation(MQLPoint p, String lerp) {
    }
}
