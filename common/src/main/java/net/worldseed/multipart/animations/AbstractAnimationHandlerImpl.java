package net.worldseed.multipart.animations;

import net.worldseed.multipart.AbstractGenericModel;
import net.worldseed.multipart.animations.data.AnimatedBoneData;
import net.worldseed.multipart.animations.data.AnimationData;
import net.worldseed.multipart.animations.data.BoneAnimationData;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractAnimationHandlerImpl implements AnimationHandler {
    private final AbstractGenericModel model;

    private final Map<String, ModelAnimation> animations = new ConcurrentHashMap<>();
    private final TreeMap<Integer, ModelAnimation> repeating = new TreeMap<>();
    private String playingOnce = null;

    private final Map<String, Runnable> callbacks = new ConcurrentHashMap<>();
    private final Map<String, Integer> callbackTimers = new ConcurrentHashMap<>();

    public AbstractAnimationHandlerImpl(AbstractGenericModel model) {
        this.model = model;
        loadDefaultAnimations();
    }

    protected void loadDefaultAnimations() {
        Map<String, AnimationData> loadedAnimations = model.getModelRegistry().getOrLoadAnimations(model.getId());
        // Init animation
        int i = 0;
        for (Map.Entry<String, AnimationData> animation : loadedAnimations.entrySet()) {
            registerAnimation(animation.getKey(), animation.getValue(), i);
            i--;
        }
    }

    @Override
    public void registerAnimation(String name, AnimationData animation, int priority) {
        final double length = animation.length();

        HashSet<BoneAnimation> animationSet = new HashSet<>();
        HashSet<String> animatedBones = new HashSet<>();

        for (Map.Entry<String, AnimatedBoneData> boneEntry : animation.bones().entrySet()) {
            String boneName = boneEntry.getKey();
            var bone = model.getPart(boneName);
            if (bone == null) continue;

            BoneAnimationData animationRotation = boneEntry.getValue().rotation();
            BoneAnimationData animationPosition = boneEntry.getValue().position();
            BoneAnimationData animationScale = boneEntry.getValue().scale();

            boolean animated = false;

            if (animationRotation != null) {
                animated = true;
                BoneAnimationImpl boneAnimation = new BoneAnimationImpl(name, boneName, bone, animationRotation, AnimationLoader.AnimationType.ROTATION, length);
                animationSet.add(boneAnimation);
            }
            if (animationPosition != null) {
                animated = true;
                BoneAnimationImpl boneAnimation = new BoneAnimationImpl(name, boneName, bone, animationPosition, AnimationLoader.AnimationType.TRANSLATION, length);
                animationSet.add(boneAnimation);
            }
            if (animationScale != null) {
                animated = true;
                BoneAnimationImpl boneAnimation = new BoneAnimationImpl(name, boneName, bone, animationScale, AnimationLoader.AnimationType.SCALE, length);
                animationSet.add(boneAnimation);
            }

            if (animated) {
                animatedBones.add(boneName);
            }
        }

        animations.put(name, new ModelAnimationClassic(name, (int) (length * 20), priority, animationSet, animatedBones));
    }

    @Override
    public void registerAnimation(ModelAnimation animator) {
        animations.put(animator.name(), animator);
    }

    public void playRepeat(String animation) throws IllegalArgumentException {
        playRepeat(animation, AnimationDirection.FORWARD);
    }

    @Override
    public void playRepeat(String animation, AnimationDirection direction) throws IllegalArgumentException {
        if (this.animationPriorities().get(animation) == null)
            throw new IllegalArgumentException("Animation " + animation + " does not exist");
        var modelAnimation = this.animations.get(animation);

        if (this.repeating.containsKey(this.animationPriorities().get(animation))
                && modelAnimation.direction() == direction) return;

        modelAnimation.setDirection(direction);

        this.repeating.put(this.animationPriorities().get(animation), modelAnimation);
        var top = this.repeating.firstEntry();

        if (top != null && animation.equals(top.getValue().name())) { //The animation you want to play is the highest priority
            this.repeating.values().forEach(v -> {
                if (!v.name().equals(animation)) { //Stop all lower priority animations to ensure the correct one is playing
                    v.stop(); //The extra loop seemed redundant, please let me know if this breaks something
                }
            });
            if (playingOnce == null) {
                modelAnimation.play(false); //Start the repeating animation if no playOnce animation is currently playing
            }
        }
    }

    public void stopRepeat(String animation) throws IllegalArgumentException {
        if (this.animationPriorities().get(animation) == null)
            throw new IllegalArgumentException("Animation " + animation + " does not exist");

        var modelAnimation = this.animations.get(animation);

        modelAnimation.stop(); //Stop the highest priority repeating animation
        int priority = this.animationPriorities().get(animation);

        Map.Entry<Integer, ModelAnimation> currentTop = this.repeating.firstEntry();

        this.repeating.remove(priority);

        Map.Entry<Integer, ModelAnimation> firstEntry = this.repeating.firstEntry();

        if (this.playingOnce == null && firstEntry != null && currentTop != null && !firstEntry.getKey().equals(currentTop.getKey())) {
            firstEntry.getValue().play(false); //Restart the new highest priority repeating animation
        }
    }


    public void playOnce(String animation, Runnable cb) throws IllegalArgumentException {
        this.playOnce(animation, true, cb);
    }

    public void playOnce(String animation, boolean override, Runnable cb) throws IllegalArgumentException {
        this.playOnce(animation, AnimationDirection.FORWARD, override, cb);
    }

    @Override
    public void playOnce(String animation, AnimationDirection direction, boolean override, Runnable cb) throws IllegalArgumentException {
        if (this.animationPriorities().get(animation) == null)
            throw new IllegalArgumentException("Animation " + animation + " does not exist");

        var modelAnimation = this.animations.get(animation);

        AnimationDirection currentDirection = modelAnimation.direction();
        modelAnimation.setDirection(direction);

        if (this.callbacks.containsKey(animation)) { //This animation had a pending runnable
            this.callbacks.get(animation).run(); //Run callback runnable
        }

        int callbackTimer = this.callbackTimers.getOrDefault(animation, 0);

        if (animation.equals(this.playingOnce) && direction == AnimationDirection.PAUSE && callbackTimer > 0) { //This animation was already playing, paused and not finished
            // Pause. Only call if we're not stopped
            playingOnce = animation;
            this.callbacks.put(animation, cb);
        } else if (animation.equals(this.playingOnce) && currentDirection != direction) { //This animation was already playing, but in a different direction
            playingOnce = animation;
            this.callbacks.put(animation, cb);
            if (currentDirection != AnimationDirection.PAUSE)
                this.callbackTimers.put(animation, modelAnimation.animationTime() - callbackTimer + 1);
        } else if (direction != AnimationDirection.PAUSE) { //This animation was not playing, or it was in the same direction
            if (playingOnce != null) { //Stop current animation
                this.animations.get(playingOnce).stop();
                modelAnimation.stop();
            }
            playingOnce = animation;

            this.callbacks.put(animation, cb);
            this.callbackTimers.put(animation, modelAnimation.animationTime());
            modelAnimation.play(false);

            Set<String> animatedBones = modelAnimation.getAnimatedBones();
            this.repeating.values().forEach(v -> {
                if (!v.name().equals(animation)) {
                    if (override) {
                        v.stop(); //Stop all repeating animations
                    } else {
                        v.stop(animatedBones); //Stop all 'animatedBones' for all repeating animations
                    }
                }
            });
        }
    }

    protected void tick() {
        try {
            for (Map.Entry<String, Integer> entry : callbackTimers.entrySet()) {
                var modelAnimation = animations.get(entry.getKey()); //Get playOnce animation from string

                if (entry.getValue() <= 0) { //All ticks were removed so playOnce should end
                    if (this.playingOnce != null && this.playingOnce.equals(entry.getKey())) {
                        Map.Entry<Integer, ModelAnimation> firstEntry = this.repeating.firstEntry();
                        if (firstEntry != null) {
                            firstEntry.getValue().play(true); //Restart or resume the highest priority repeating animation
                        }
                        this.playingOnce = null;
                    }

                    this.model.triggerAnimationEnd(entry.getKey(), modelAnimation.direction()); //Call AnimationCompleteEvent

                    modelAnimation.stop();
                    callbackTimers.remove(entry.getKey()); //Remove playOnce animation from map

                    var cb = callbacks.remove(entry.getKey());
                    if (cb != null) cb.run(); //Run 'callback' runnable
                } else {
                    if (modelAnimation.direction() != AnimationDirection.PAUSE) {
                        callbackTimers.put(entry.getKey(), entry.getValue() - 1); //Countdown 1 tick until it reaches 0 during playOnce animation
                    }
                }
            }

            if (callbacks.size() + repeating.size() == 0) return; //Return if no playOnce or repeating animation is playing
            this.model.draw(); 

            this.animations.forEach((animation, animations) -> {
                animations.tick(); //Play every tick (besides the first one) of the animation
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void destroy();

    @Override
    public @Nullable String getPlaying() {
        if (this.playingOnce != null) return this.playingOnce;
        return getRepeating();
    }

    @Override
    public @Nullable String getRepeating() {
        var playing = this.repeating.firstEntry();
        return playing != null ? playing.getValue().name() : null;
    }

    @Override
    public @Nullable ModelAnimation getAnimation(String animation) {
        return this.animations.get(animation);
    }

    @Override
    public Map<String, Integer> animationPriorities() {
        return new HashMap<>() {{
            for (Map.Entry<String, ModelAnimation> entry : animations.entrySet()) {
                put(entry.getKey(), entry.getValue().priority());
            }
        }};
    }
}
