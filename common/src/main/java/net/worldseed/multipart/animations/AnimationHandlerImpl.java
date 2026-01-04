package net.worldseed.multipart.animations;

import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.animations.data.AnimatedBoneData;
import net.worldseed.multipart.animations.data.AnimationData;
import net.worldseed.multipart.animations.data.BoneAnimationData;
import net.worldseed.multipart.scheduling.ScheduledTask;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnimationHandlerImpl<TViewer> implements AnimationHandler {
    private final GenericModel<TViewer> model;

    private final Map<String, ModelAnimation> animations = new ConcurrentHashMap<>();
    private final TreeMap<Integer, ModelAnimation> repeating = new TreeMap<>();
    private ModelAnimation playingOnce = null;

    private final Map<String, Runnable> callbacks = new ConcurrentHashMap<>();
    private final Map<String, Integer> callbackTimers = new ConcurrentHashMap<>();
    private final ScheduledTask task;

    public AnimationHandlerImpl(GenericModel<TViewer> model) {
        this.model = model;
        loadDefaultAnimations();
        this.task = model.getModelPlatform().getScheduler(model).syncRepeating(this::tick, 0, 1);
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

    @Override
    public void playRepeat(String animation, AnimationDirection direction, short startAt) throws IllegalArgumentException {
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
                    this.model.triggerAnimationStopped(v, v.direction(), true);
                    v.stop(); //The extra loop seemed redundant, please let me know if this breaks something
                }
            });
            if (playingOnce == null) {
                if(startAt != -1) {
                    modelAnimation.play(startAt);
                } else {
                    modelAnimation.play(false); //Start the repeating animation if no playOnce animation is currently playing
                }
                this.model.triggerAnimationStart(modelAnimation, modelAnimation.direction(), modelAnimation.getTick(), true);
            }
        }
    }

    @Override
    public void stop(String animation) throws IllegalArgumentException {
        var modelAnimation = this.animations.get(animation);
        if (modelAnimation == null) {
            throw new IllegalArgumentException("Animation " + animation + " does not exist");
        }

        if (modelAnimation.equals(this.playingOnce)) {
            this.model.triggerAnimationStopped(modelAnimation, modelAnimation.direction(), false);
            this.playingOnce = null;
            modelAnimation.stop();

            Map.Entry<Integer, ModelAnimation> firstEntry = this.repeating.firstEntry();
            if (firstEntry != null) {
                boolean wasPlaying = firstEntry.getValue().isPlaying();
                firstEntry.getValue().play(true); //Restart or resume the highest priority repeating animation
                if(!wasPlaying) {
                    this.model.triggerAnimationStart(firstEntry.getValue(), firstEntry.getValue().direction(), firstEntry.getValue().getTick(), true);
                }
            }
        }
    }

    public void stopRepeat(String animation) throws IllegalArgumentException {
        var modelAnimation = this.animations.get(animation);
        if (modelAnimation == null) {
            throw new IllegalArgumentException("Animation " + animation + " does not exist");
        }

        if(!repeating.containsValue(modelAnimation)) {
            throw new IllegalArgumentException("Animation " + animation + " is not playing as a repeating animation");
        }

        this.model.triggerAnimationStopped(modelAnimation, modelAnimation.direction(), true);
        modelAnimation.stop(); //Stop the highest priority repeating animation

        int priority = this.animationPriorities().get(animation);
        Map.Entry<Integer, ModelAnimation> currentTop = this.repeating.firstEntry();

        this.repeating.remove(priority);

        Map.Entry<Integer, ModelAnimation> firstEntry = this.repeating.firstEntry();

        if (this.playingOnce == null && firstEntry != null && currentTop != null && !firstEntry.getKey().equals(currentTop.getKey())) {
            firstEntry.getValue().play(false); //Restart the new highest priority repeating animation
            this.model.triggerAnimationStart(firstEntry.getValue(), firstEntry.getValue().direction(), firstEntry.getValue().getTick(), true);
        }
    }

    @Override
    public void playOnce(String animation, AnimationDirection direction, boolean override, short startAt, Runnable cb) throws IllegalArgumentException {
        if (this.animationPriorities().get(animation) == null)
            throw new IllegalArgumentException("Animation " + animation + " does not exist");

        var modelAnimation = this.animations.get(animation);

        AnimationDirection currentDirection = modelAnimation.direction();
        modelAnimation.setDirection(direction);

        if (this.callbacks.containsKey(animation)) { //This animation had a pending runnable
            this.callbacks.get(animation).run(); //Run callback runnable
        }

        int callbackTimer = this.callbackTimers.getOrDefault(animation, 0);

        if (modelAnimation.equals(this.playingOnce) && direction == AnimationDirection.PAUSE && callbackTimer > 0) { //This animation was already playing, paused and not finished
            // Pause. Only call if we're not stopped
            playingOnce = modelAnimation;
            this.callbacks.put(animation, cb);
        } else if (modelAnimation.equals(this.playingOnce) && currentDirection != direction) { //This animation was already playing, but in a different direction
            playingOnce = modelAnimation;
            this.callbacks.put(animation, cb);
            if (currentDirection != AnimationDirection.PAUSE)
                this.callbackTimers.put(animation, modelAnimation.animationTime() - callbackTimer + 1);
        } else if (direction != AnimationDirection.PAUSE) { //This animation was not playing, or it was in the same direction
            int totalTime = modelAnimation.animationTime();
            if(startAt != -1) {
                if(direction == AnimationDirection.FORWARD) {
                    totalTime -= startAt;
                } else if(direction == AnimationDirection.BACKWARD) {
                    totalTime = startAt;
                }
            }
            if(totalTime <= 0) throw new IllegalArgumentException("Animation " + animation + " has no time to play from the given start position");

            if (playingOnce != null) { //Stop current animation
                this.model.triggerAnimationStopped(playingOnce, playingOnce.direction(), false);
                playingOnce.stop();
                modelAnimation.stop();
            }
            playingOnce = modelAnimation;

            this.callbacks.put(animation, cb);
            this.callbackTimers.put(animation, totalTime);

            if(startAt != -1) {
                modelAnimation.play(startAt);
            } else {
                modelAnimation.play(false);
            }
            this.model.triggerAnimationStart(modelAnimation, modelAnimation.direction(), modelAnimation.getTick(), false);

            Set<String> animatedBones = modelAnimation.getAnimatedBones();
            this.repeating.values().forEach(v -> {
                if (!v.name().equals(animation)) {
                    if (override) {
                        this.model.triggerAnimationStopped(v, v.direction(), true);
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
                    if (this.playingOnce != null && this.playingOnce.name().equals(entry.getKey())) {
                        Map.Entry<Integer, ModelAnimation> firstEntry = this.repeating.firstEntry();
                        if (firstEntry != null) {
                            boolean wasPlaying = firstEntry.getValue().isPlaying();
                            firstEntry.getValue().play(true); //Restart or resume the highest priority repeating animation

                            if(!wasPlaying) {
                                this.model.triggerAnimationStart(firstEntry.getValue(), firstEntry.getValue().direction(), firstEntry.getValue().getTick(), true);
                            }
                        }
                        this.playingOnce = null;
                    }

                    this.model.triggerAnimationComplete(modelAnimation, modelAnimation.direction()); //Call AnimationCompleteEvent
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

    public void destroy() {
        this.task.cancel();
    }

    @Override
    public @Nullable String getPlaying() {
        ModelAnimation playingAnimation = getPlayingAnimation();
        return playingAnimation != null ? playingAnimation.name() : null;
    }

    @Override
    public @Nullable ModelAnimation getPlayingOnceAnimation() {
        return this.playingOnce;
    }

    @Override
    public @Nullable ModelAnimation getPlayingAnimation() {
        if (this.playingOnce != null) return this.playingOnce;
        return getRepeatingAnimation();
    }

    @Override
    public @Nullable ModelAnimation getRepeatingAnimation() {
        var playing = this.repeating.firstEntry();
        return playing != null ? playing.getValue() : null;
    }

    @Override
    public @Nullable String getRepeating() {
        ModelAnimation repeatingAnimation = getRepeatingAnimation();
        return repeatingAnimation != null ? repeatingAnimation.name() : null;
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
