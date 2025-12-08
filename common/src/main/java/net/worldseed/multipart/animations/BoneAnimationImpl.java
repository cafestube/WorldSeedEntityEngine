package net.worldseed.multipart.animations;

import net.worldseed.multipart.animations.data.BoneAnimationData;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.entity.ModelBone;
import net.worldseed.multipart.mql.MQLPoint;
import org.jetbrains.annotations.NotNull;

public class BoneAnimationImpl implements BoneAnimation {
    private final AnimationLoader.AnimationType type;

    private final FrameProvider frameProvider;
    private final int length;
    private final String name;
    private final String boneName;
    private boolean playing = false;
    private short tick = 0;
    private AnimationHandler.AnimationDirection direction = AnimationHandler.AnimationDirection.FORWARD;

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
            if (direction == AnimationHandler.AnimationDirection.FORWARD) {
                tick++;
                if (tick > length && length != 0) tick = 0;
            } else if (direction == AnimationHandler.AnimationDirection.BACKWARD) {
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

    public void setDirection(AnimationHandler.AnimationDirection direction) {
        this.direction = direction;
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

    public record KeyFrame(double time, MQLPoint p, Interpolation lerp) implements Comparable<KeyFrame> {

        @Override
        public int compareTo(@NotNull BoneAnimationImpl.KeyFrame o) {
            return Double.compare(time, o.time);
        }

    }
}
