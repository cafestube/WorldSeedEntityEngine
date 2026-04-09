package net.worldseed.multipart.animations;

import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.blueprint.animation.AnimationData;
import net.worldseed.multipart.entity.ModelBone;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.scheduling.ScheduledTask;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class AnimationHandlerImpl<TViewer> implements AnimationHandler {
    private final GenericModel<TViewer> model;

    private final Map<String, ModelAnimationInstance> animations = new ConcurrentHashMap<>();
    private final List<ModelAnimationInstance> animationsSortedDescending = new ArrayList<>();
    private final ScheduledTask task;

    public AnimationHandlerImpl(GenericModel<TViewer> model) {
        this.model = model;
        loadDefaultAnimations();
        this.task = model.getModelPlatform().getScheduler(model).syncRepeating(this::tick, 0, 1);
    }

    protected void loadDefaultAnimations() {
        Map<String, AnimationData> loadedAnimations = model.getBlueprint().animations();
        // Init animation
        int i = 0;
        for (Map.Entry<String, AnimationData> animation : loadedAnimations.entrySet()) {
            registerAnimation(animation.getKey(), animation.getValue(), i);
            i--;
        }
    }

    @Override
    public void registerAnimation(String name, AnimationData animation, int priority) {
        registerAnimation(new ModelAnimationInstanceImpl(model, name, animation, priority));
    }

    @Override
    public void registerAnimation(ModelAnimationInstance animator) {
        animations.put(animator.name(), animator);

        animationsSortedDescending.add(animator);
        animationsSortedDescending.sort((o1, o2)
                -> Integer.compare(o2.priority(), o1.priority()));
    }

    @Override
    public void stop(String animation) throws IllegalArgumentException {
        animations.get(animation).stop();
    }

    @Override
    public void forceStop(String animation) throws IllegalArgumentException {
        animations.get(animation).forceStop();
    }

    @Override
    public void pause(String animation) throws IllegalArgumentException {
        animations.get(animation).pause();
    }

    @Override
    public void resume(String animation) throws IllegalArgumentException {
        animations.get(animation).resume();
    }

    @Override
    public void updateBone(ModelBone<?> bone) {
        Point translation = Vec.ZERO;
        Point rotation = Vec.ZERO;
        Point scale = Vec.ONE;

        for (ModelAnimationInstance modelAnimationInstance : animationsSortedDescending) {
            //Avoid allocations by just not doing any calculation for these
            if(!modelAnimationInstance.getAnimatedBones().contains(bone.getName()))
                continue;
            if(!modelAnimationInstance.isActive()) continue;

            Point thisTranslation = modelAnimationInstance.getTranslation(bone.getName());
            Point thisRotation = modelAnimationInstance.getRotation(bone.getName());
            Point thisScale = modelAnimationInstance.getScale(bone.getName());

            switch (modelAnimationInstance.getState()) {
                case FADE_IN -> {
                    thisTranslation = Vec.ZERO.lerp(
                            thisTranslation,
                            modelAnimationInstance.getFadeInPercent()
                    );
                    thisRotation = Vec.ZERO.lerp(
                            thisRotation,
                            modelAnimationInstance.getFadeInPercent()
                    );
                    thisScale = Vec.ONE.lerp(
                            thisScale,
                            modelAnimationInstance.getFadeInPercent()
                    );
                }
                case FADE_OUT -> {
                    thisTranslation = thisTranslation.lerp(
                            Vec.ZERO,
                            modelAnimationInstance.getFadeOutPercent()
                    );
                    thisRotation = thisRotation.lerp(
                            Vec.ZERO,
                            modelAnimationInstance.getFadeOutPercent()
                    );
                    thisScale = thisScale.lerp(
                            Vec.ONE,
                            modelAnimationInstance.getFadeOutPercent()
                    );
                }
            }

            if(modelAnimationInstance.isOverrideBones()) {
                translation = thisTranslation;
                rotation = thisRotation;
                scale = thisScale;
            } else {
                translation = translation.add(thisTranslation);
                rotation = rotation.add(thisRotation);
                scale = scale.mul(thisScale);
            }
        }

        if(!translation.equals(Vec.ZERO) || !rotation.equals(Vec.ZERO) || !scale.equals(Vec.ONE)) {
            bone.setAnimationTransform(new BoneAnimationTransform(translation, rotation, scale));
        } else {
            bone.setAnimationTransform(BoneAnimationTransform.ZERO);
        }
    }

    @Override
    public void playAnimation(String animation, AnimationDirection direction, boolean overrideBones, boolean repeating, int startAt, int fadeInDuration, int fadeOutDuration, @Nullable Runnable onEnd) throws IllegalArgumentException {
        ModelAnimationInstance animationInstance = this.animations.get(animation);
        animationInstance.reset();
        animationInstance.setOverrideBones(overrideBones);
        animationInstance.setRepeating(repeating);
        animationInstance.setFadeTiming(fadeInDuration, fadeOutDuration);
        animationInstance.setEndCallback(onEnd);

        animationInstance.start(startAt);
    }

    @Override
    public void playAnimation(String animation, boolean repeating, @Nullable Runnable onEnd) throws IllegalArgumentException {
        ModelAnimationInstance animationInstance = this.animations.get(animation);
        animationInstance.reset();
        animationInstance.setRepeating(repeating);
        animationInstance.setEndCallback(onEnd);

        animationInstance.start();
    }

    @Override
    public void playAnimation(String animation, AnimationDirection direction, boolean overrideBones, boolean repeating, @Nullable Runnable onEnd) throws IllegalArgumentException {
        ModelAnimationInstance animationInstance = this.animations.get(animation);
        animationInstance.reset();
        animationInstance.setOverrideBones(overrideBones);
        animationInstance.setRepeating(repeating);
        animationInstance.setEndCallback(onEnd);

        animationInstance.start();
    }

    @Override
    public void playAnimation(String animation, AnimationDirection direction, int startAt, int fadeInDuration, int fadeOutDuration, @Nullable Runnable onEnd) throws IllegalArgumentException {
        ModelAnimationInstance animationInstance = this.animations.get(animation);
        animationInstance.reset();
        animationInstance.setFadeTiming(fadeInDuration, fadeOutDuration);
        animationInstance.setEndCallback(onEnd);

        animationInstance.start(startAt);
    }

    @Override
    public void playAnimation(String animation, AnimationDirection direction, boolean repeating, int startAt, int fadeInDuration, int fadeOutDuration, @Nullable Runnable onEnd) throws IllegalArgumentException {
        ModelAnimationInstance animationInstance = this.animations.get(animation);
        animationInstance.reset();
        animationInstance.setRepeating(repeating);
        animationInstance.setFadeTiming(fadeInDuration, fadeOutDuration);
        animationInstance.setEndCallback(onEnd);

        animationInstance.start(startAt);
    }

    private boolean animationRunningLastTick;

    protected void tick() {
        try {
            boolean animationRunning = false;
            for (Map.Entry<String, ModelAnimationInstance> animation : this.animations.entrySet()) {
                if(animation.getValue().isPlaying()) {
                    animationRunning = true;
                }
            }

            if(animationRunning || animationRunningLastTick) {
                this.model.draw();
            }

            for (Map.Entry<String, ModelAnimationInstance> animation : this.animations.entrySet()) {
                if(!animation.getValue().isPlaying()) continue;
                animation.getValue().tick();
            }

            this.animationRunningLastTick = animationRunning;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        this.task.cancel();
    }

    @Override
    public @Nullable ModelAnimationInstance getAnimation(String animation) {
        return this.animations.get(animation);
    }

    @Override
    public List<ModelAnimationInstance> getPlayingAnimations() {
        ArrayList<ModelAnimationInstance> list = new ArrayList<>(animations.size());
        for (ModelAnimationInstance value : this.animations.values()) {
            if(value.isPlaying()) list.add(value);
        }
        return list;
    }

    @Override
    public List<String> getPlayingAnimationNames() {
        ArrayList<String> list = new ArrayList<>(animations.size());
        for (ModelAnimationInstance value : this.animations.values()) {
            if(value.isPlaying()) list.add(value.name());
        }
        return list;
    }

    @Override
    public List<ModelAnimationInstance> getPlayingOnceAnimations() {
        ArrayList<ModelAnimationInstance> list = new ArrayList<>(animations.size());
        for (ModelAnimationInstance value : this.animations.values()) {
            if(value.isPlaying() && !value.isRepeating()) list.add(value);
        }
        return list;
    }

    @Override
    public List<String> getPlayingOnceAnimationNames() {
        ArrayList<String> list = new ArrayList<>(animations.size());
        for (ModelAnimationInstance value : this.animations.values()) {
            if(value.isPlaying() && !value.isRepeating()) list.add(value.name());
        }
        return list;
    }

    @Override
    public List<ModelAnimationInstance> getRepeatingAnimations() {
        ArrayList<ModelAnimationInstance> list = new ArrayList<>(animations.size());
        for (ModelAnimationInstance value : this.animations.values()) {
            if(value.isPlaying() && value.isRepeating()) list.add(value);
        }
        return list;
    }

    @Override
    public List<String> getRepeatingAnimationNames() {
        ArrayList<String> list = new ArrayList<>(animations.size());
        for (ModelAnimationInstance value : this.animations.values()) {
            if(value.isPlaying() && value.isRepeating()) list.add(value.name());
        }
        return list;
    }

    @Override
    public Map<String, Integer> animationPriorities() {
        return new HashMap<>() {{
            for (Map.Entry<String, ModelAnimationInstance> entry : animations.entrySet()) {
                put(entry.getKey(), entry.getValue().priority());
            }
        }};
    }
}
