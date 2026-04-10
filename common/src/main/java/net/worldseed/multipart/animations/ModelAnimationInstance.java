package net.worldseed.multipart.animations;

import net.worldseed.multipart.blueprint.animation.AnimationData;
import net.worldseed.multipart.math.Point;

import java.util.Set;

public interface ModelAnimationInstance {

    AnimationData getAnimation();

    int priority();

    int animationTime();

    String name();

    void setRepeating(boolean repeating);

    boolean isRepeating();

    boolean isOverrideBones();

    void setOverrideBones(boolean override);

    default Set<String> getAnimatedBones() {
        return getAnimation().bones().keySet();
    }

    AnimationHandler.AnimationDirection direction();

    void setDirection(AnimationHandler.AnimationDirection direction);

    void reset();

    void setEndCallback(Runnable rb);

    void setFadeTiming(int fadeIn, int fadeOut);

    int getFadeIn();

    int getFadeOut();

    double getFadeInPercent();

    double getFadeOutPercent();

    AnimationState getState();

    /**
     * Returns weather the animation is currently progressing. Use {@see isActive} to check if the animation
     * is currently being shown
     */
    boolean isPlaying();

    /**
     * Returns weather the animation is currently active or has ended.
     */
    boolean isActive();

    boolean isEnding();

    void stop();

    void forceStop();

    void resume();

    void pause();

    void start(int tick);

    void start();

    void tick();

    int getTick();

    Point getTranslation(String name);

    Point getRotation(String name);

    boolean isRotationGlobal(String name);

    Point getScale(String name);

    enum AnimationState {
        FADE_IN, PLAYING, FADE_OUT
    }
}
