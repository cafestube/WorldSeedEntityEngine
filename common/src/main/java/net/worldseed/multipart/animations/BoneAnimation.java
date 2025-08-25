package net.worldseed.multipart.animations;

import net.worldseed.multipart.math.Point;

public interface BoneAnimation {
    String name();

    String boneName();

    AnimationLoader.AnimationType getType();

    Point getTransformAtTime(int time);

    boolean isPlaying();

    Point getTransform();

    void setDirection(AnimationHandler.AnimationDirection direction);

    void stop();

    void play();

    void tick();
    void resume(short tick);
    short getTick();
}
