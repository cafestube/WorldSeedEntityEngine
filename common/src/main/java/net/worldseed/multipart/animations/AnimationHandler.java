package net.worldseed.multipart.animations;

import net.worldseed.multipart.animations.data.AnimationData;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface AnimationHandler {
    void registerAnimation(String name, AnimationData animation, int priority);

    void registerAnimation(ModelAnimation animator);

    /**
     * Play an animation on repeat
     *
     * @param animation name of animation to play
     */
    default void playRepeat(String animation) throws IllegalArgumentException {
        playRepeat(animation, AnimationDirection.FORWARD);
    }

    default void playRepeat(String animation, AnimationDirection direction) throws IllegalArgumentException {
        playRepeat(animation, direction, (short) -1);
    }

    void playRepeat(String animation, AnimationDirection direction, short startAt) throws IllegalArgumentException;

    /**
     * Stop a repeating animation
     *
     * @param animation name of animation to stop
     */
    void stop(String animation) throws IllegalArgumentException;

    /**
     * Play an animation once
     *
     * @param animation name of animation to play
     * @param override If true (default), fully overrides repeating background animations. If false, overrides only bones used in new animation.
     * @param cb       callback to call when animation is finished
     */
    default void playOnce(String animation, boolean override, Runnable cb) throws IllegalArgumentException {
        this.playOnce(animation, AnimationDirection.FORWARD, override, cb);
    }

    void stopRepeat(String animation) throws IllegalArgumentException;

    /**
     * Play an animation once
     *
     * @param animation name of animation to play
     * @param cb        callback to call when animation is finished
     */
    default void playOnce(String animation, Runnable cb) throws IllegalArgumentException {
        this.playOnce(animation, true, cb);
    }

    default void playOnce(String animation, AnimationHandler.AnimationDirection direction, boolean override, Runnable cb) throws IllegalArgumentException {
        playOnce(animation, direction, override, (short) -1, cb);
    }

    void playOnce(String animation, AnimationHandler.AnimationDirection direction, boolean override, short startAt, Runnable cb) throws IllegalArgumentException;

    /**
     * Destroy the animation handler
     */
    void destroy();

    /**
     * Get the current animation
     *
     * @return current animation
     */
    @Nullable String getPlaying();

    @Nullable ModelAnimation getPlayingOnceAnimation();

    @Nullable ModelAnimation getPlayingAnimation();


    /**
     * Get the current repeating animation
     *
     * @return current repeating animation
     */
    @Nullable String getRepeating();

    @Nullable ModelAnimation getRepeatingAnimation();

    /**
     * Get an animation by key
     *
     * @return animation object
     */
    @Nullable ModelAnimation getAnimation(String animation);

    Map<String, Integer> animationPriorities();

    enum AnimationDirection {
        FORWARD,
        BACKWARD,
        PAUSE
    }
}
