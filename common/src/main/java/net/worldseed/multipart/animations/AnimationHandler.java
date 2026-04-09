package net.worldseed.multipart.animations;

import net.worldseed.multipart.blueprint.animation.AnimationData;
import net.worldseed.multipart.entity.ModelBone;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface AnimationHandler {

    void registerAnimation(String name, AnimationData animation, int priority);

    void registerAnimation(ModelAnimationInstance animator);

    /**
     * Stop a animation
     *
     * @param animation name of animation to stop
     */
    void stop(String animation) throws IllegalArgumentException;

    void forceStop(String animation) throws IllegalArgumentException;

    void pause(String animation) throws IllegalArgumentException;

    void resume(String animation) throws IllegalArgumentException;

    /**
     * Updates the bone's transforms
     */
    void updateBone(ModelBone<?> bone);

    /**
     * Plays an animation
     *
     * @param animation The animation to play
     * @param repeating whether to repeat the animation more than once
     * @param onEnd runnable to be called after the animation either finished normally or has been stopped
     */
    void playAnimation(
            String animation,
            boolean repeating,
            @Nullable Runnable onEnd
    ) throws IllegalArgumentException;

    /**
     * Plays an animation
     *
     * @param animation The animation to play
     * @param repeating whether to repeat the animation more than once
     */
    default void playAnimation(
            String animation,
            boolean repeating
    ) throws IllegalArgumentException {
        playAnimation(animation, repeating, null);
    }

    /**
     * Plays an animation
     *
     * @param animation The animation to play
     * @param direction the direction to play the animation in
     * @param overrideBones whether to override bone animation state or merge it with other running animations
     * @param repeating whether to repeat the animation more than once
     * @param startAt tick to start the animation at
     * @param fadeInDuration ticks to transition to the new animation
     * @param fadeOutDuration ticks to transition out of the animation after it finished
     * @param onEnd runnable to be called after the animation either finished normally or has been stopped
     */
    void playAnimation(
            String animation,
            AnimationDirection direction,
            boolean overrideBones,
            boolean repeating,
            int startAt,
            int fadeInDuration,
            int fadeOutDuration,
            @Nullable Runnable onEnd
    ) throws IllegalArgumentException;

    /**
     * Plays an animation
     *
     * @param animation The animation to play
     * @param direction the direction to play the animation in
     * @param overrideBones whether to override bone animation state or merge it with other running animations
     * @param repeating whether to repeat the animation more than once
     * @param onEnd runnable to be called after the animation either finished normally or has been stopped
     */
    void playAnimation(
            String animation,
            AnimationDirection direction,
            boolean overrideBones,
            boolean repeating,
            @Nullable Runnable onEnd
    ) throws IllegalArgumentException;

    /**
     * Plays an animation with override and repeating values from the provided animation.
     *
     * @param animation The animation to play
     * @param direction the direction to play the animation in
     * @param startAt tick to start the animation at
     * @param fadeInDuration ticks to transition to the new animation
     * @param fadeOutDuration ticks to transition out of the animation after it finished
     * @param onEnd runnable to be called after the animation either finished normally or has been stopped
     */
    void playAnimation(
            String animation,
            AnimationDirection direction,
            int startAt,
            int fadeInDuration,
            int fadeOutDuration,
            @Nullable Runnable onEnd
    ) throws IllegalArgumentException;

    /**
     * Plays an animation with override and repeating values from the provided animation.
     *
     * @param animation The animation to play
     * @param direction the direction to play the animation in
     * @param startAt tick to start the animation at
     * @param fadeInDuration ticks to transition to the new animation
     * @param fadeOutDuration ticks to transition out of the animation after it finished
     * @param repeating whether to repeat the animation more than once
     * @param onEnd runnable to be called after the animation either finished normally or has been stopped
     */
    void playAnimation(
            String animation,
            AnimationDirection direction,
            boolean repeating,
            int startAt,
            int fadeInDuration,
            int fadeOutDuration,
            @Nullable Runnable onEnd
    ) throws IllegalArgumentException;


    /**
     * Destroy the animation handler
     */
    void destroy();

    /**
     * Get an animation by key
     *
     * @return animation object
     */
    @Nullable ModelAnimationInstance getAnimation(String animation);

    List<ModelAnimationInstance> getPlayingAnimations();

    List<String> getPlayingAnimationNames();

    List<ModelAnimationInstance> getPlayingOnceAnimations();

    List<String> getPlayingOnceAnimationNames();

    List<ModelAnimationInstance> getRepeatingAnimations();

    List<String> getRepeatingAnimationNames();

    Map<String, Integer> animationPriorities();

    enum AnimationDirection {
        FORWARD,
        BACKWARD
    }

    /*
     * Deprecated methods pre-multiple animation handling to keep backwards compat for now
     */


    /**
     * Play an animation once
     *
     * @param animation name of animation to play
     * @param override If true (default), fully overrides repeating background animations. If false, overrides only bones used in new animation.
     * @param cb       callback to call when animation is finished
     */
    @Deprecated
    default void playOnce(String animation, boolean override, Runnable cb) throws IllegalArgumentException {
        this.playOnce(animation, AnimationDirection.FORWARD, override, cb);
    }

    @Deprecated
    default void stopRepeat(String animation) throws IllegalArgumentException {
        forceStop(animation);
    }

    /**
     * Play an animation on repeat
     *
     * @param animation name of animation to play
     */
    @Deprecated
    default void playRepeat(String animation) throws IllegalArgumentException {
        playRepeat(animation, AnimationDirection.FORWARD);
    }

    @Deprecated
    default void playRepeat(String animation, AnimationDirection direction) throws IllegalArgumentException {
        playRepeat(animation, direction, (short) -1);
    }

    @Deprecated
    default void playRepeat(String animation, AnimationDirection direction, short startAt) throws IllegalArgumentException {
        playAnimation(
                animation,
                direction,
                true,
                true,
                startAt,
                0,
                0,
                null
        );
    }


    /**
     * Play an animation once
     *
     * @param animation name of animation to play
     * @param cb        callback to call when animation is finished
     */
    @Deprecated
    default void playOnce(String animation, Runnable cb) throws IllegalArgumentException {
        this.playOnce(animation, true, cb);
    }

    @Deprecated
    default void playOnce(String animation, AnimationHandler.AnimationDirection direction, boolean override, Runnable cb) throws IllegalArgumentException {
        playOnce(animation, direction, override, (short) -1, cb);
    }

    @Deprecated
    default void playOnce(String animation, AnimationHandler.AnimationDirection direction, boolean override, short startAt, Runnable cb) throws IllegalArgumentException {
        for (ModelAnimationInstance playingOnceAnimation : getPlayingOnceAnimations()) {
            forceStop(playingOnceAnimation.name());
        }

        playAnimation(
                animation,
                direction,
                override,
                false,
                startAt,
                0,
                0,
                cb
        );
    }

    /**
     * Get the current animation
     *
     * @return current animation
     */
    default @Deprecated @Nullable String getPlaying() {
        return getPlayingOnceAnimations().stream().findFirst().map(ModelAnimationInstance::name).orElse(null);
    }

    default @Deprecated @Nullable ModelAnimationInstance getPlayingOnceAnimation() {
        return getPlayingOnceAnimations().stream().findFirst().orElse(null);
    }

    default @Deprecated @Nullable ModelAnimationInstance getPlayingAnimation() {
        return getPlayingAnimations().stream().findFirst().orElse(null);
    }


    /**
     * Get the current repeating animation
     *
     * @return current repeating animation
     */
    default @Deprecated @Nullable String getRepeating() {
        ModelAnimationInstance repeatingAnimation = getRepeatingAnimation();
        if(repeatingAnimation == null) return null;
        return repeatingAnimation.name();
    }

    default @Deprecated @Nullable ModelAnimationInstance getRepeatingAnimation() {
        return getRepeatingAnimations().stream().findFirst().orElse(null);
    }
}
