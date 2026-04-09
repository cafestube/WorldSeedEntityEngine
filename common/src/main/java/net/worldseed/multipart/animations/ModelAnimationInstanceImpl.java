package net.worldseed.multipart.animations;

import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.blueprint.animation.AnimatedBoneData;
import net.worldseed.multipart.blueprint.animation.AnimationData;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;

public class ModelAnimationInstanceImpl implements ModelAnimationInstance {
    private final int priority;
    private final String name;
    private AnimationHandler.AnimationDirection direction;

    private Runnable endCallback;
    private AnimationState state;
    private AnimationData animation;

    private boolean repeating;
    private boolean overrideBones;
    private boolean paused;
    private boolean stopping;
    private boolean stopped;

    private int tick;
    private int fadeInTicks;
    private int fadeOutTicks;
    private int fadeIn = 0;
    private int fadeOut = 0;

    private GenericModel<?> model;

    public ModelAnimationInstanceImpl(GenericModel<?> model, String name, AnimationData animation, int priority) {
        this.animation = animation;
        this.name = name;
        this.model = model;

        this.priority = priority;
        reset();
    }

    @Override
    public void reset() {
        this.state = AnimationState.FADE_IN;
        this.paused = true;
        this.stopped = true;
        this.stopping = false;
        this.direction = AnimationHandler.AnimationDirection.FORWARD;
        this.repeating = animation.loop();
        this.overrideBones = animation.overrideBones();
        this.endCallback = null;

        this.fadeIn = 0;
        this.fadeOut = 0;
        this.fadeInTicks = 0;
        this.fadeOutTicks = 0;
    }

    @Override
    public void tick() {
        if(this.paused) return;

        if(this.stopping) {
            this.state = AnimationState.FADE_OUT;
        }

        switch (this.state) {
            case FADE_IN -> {
                if(fadeInTicks >= getFadeIn()) {
                    this.state = AnimationState.PLAYING;
                    this.tick = getStartTick();
                    tickAnimation();
                } else {
                    this.fadeInTicks++;
                }
            }
            case PLAYING -> tickAnimation();
            case FADE_OUT -> tickFadeOut();
        }
    }

    @Override
    public void stop() {
        this.stopping = true;
    }

    @Override
    public void start() {
        start(-1);
    }

    @Override
    public boolean isActive() {
        return !stopped;
    }

    @Override
    public boolean isEnding() {
        return stopping;
    }

    @Override
    public void start(int tick) {
        if(tick == -1) tick = getStartTick();

        if(!stopped) throw new IllegalStateException("Animation is already running");

        this.tick = tick;
        this.paused = false;
        this.stopping = false;
        this.stopped = false;

        if(tick == getStartTick()) {
            this.fadeInTicks = 0;
            this.fadeOutTicks = 0;
        } else {
            this.fadeInTicks = fadeIn;
        }

        if(this.fadeIn == 0) {
            this.state = AnimationState.PLAYING;
        } else {
            this.state = AnimationState.FADE_IN;
        }
    }

    @Override
    public void pause() {
        if(stopped) throw new IllegalStateException("Animation is not running");
        this.paused = true;
    }

    @Override
    public void resume() {
        if(stopped) throw new IllegalStateException("Animation is not running");
        this.paused = false;
    }

    @Override
    public Point getRotation(String bone) {
        AnimatedBoneData animatedBoneData = animation.bones().get(bone);
        if(animatedBoneData == null) return Vec.ZERO;
        if(animatedBoneData.rotation() == null) return Vec.ZERO;

        return animatedBoneData.rotation().frameProvider().getFrame(this.tick);
    }

    @Override
    public Point getScale(String bone) {
        AnimatedBoneData animatedBoneData = animation.bones().get(bone);
        if(animatedBoneData == null) return Vec.ONE;
        if(animatedBoneData.scale() == null) return Vec.ONE;

        return animatedBoneData.scale().frameProvider().getFrame(this.tick);
    }

    @Override
    public Point getTranslation(String bone) {
        AnimatedBoneData animatedBoneData = animation.bones().get(bone);
        if(animatedBoneData == null) return Vec.ZERO;
        if(animatedBoneData.position() == null) return Vec.ZERO;

        return animatedBoneData.position().frameProvider().getFrame(this.tick);
    }

    private void tickFadeOut() {
        if(fadeOutTicks >= getFadeOut()) {
            forceStop();
        } else {
            this.fadeOutTicks++;
        }
    }

    @Override
    public void forceStop() {
        this.stopped = true;
        if(this.endCallback != null) {
            this.endCallback.run();
        }
        this.endCallback = null;
        this.model.triggerAnimationComplete(this, this.direction()); //Call AnimationCompleteEvent

        reset();
    }

    private void tickAnimation() {

        switch (this.direction) {

            case FORWARD -> {
                if(this.tick > animationTime() && animationTime() != 0) {
                    if(this.repeating) {
                        this.tick = getStartTick();
                    } else {
                        this.state = AnimationState.FADE_OUT;
                        tickFadeOut();

                        return;
                    }
                }
                this.tick++;
            }
            case BACKWARD -> {
                if(this.tick < 0 && animationTime() != 0) {
                    if(this.repeating) {
                        this.tick = getStartTick();
                    } else {
                        this.state = AnimationState.FADE_OUT;
                        tickFadeOut();
                        return;
                    }
                }
                this.tick--;
            }
        }
    }


    private int getStartTick() {
        return switch (this.direction) {
            case FORWARD -> 0;
            case BACKWARD -> animationTime()-1;
        };
    }

    @Override
    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    @Override
    public boolean isRepeating() {
        return repeating;
    }

    @Override
    public boolean isOverrideBones() {
        return this.overrideBones;
    }

    @Override
    public void setOverrideBones(boolean override) {
        this.overrideBones = override;
    }

    public void setEndCallback(Runnable endCallback) {
        this.endCallback = endCallback;
    }

    @Override
    public void setFadeTiming(int fadeIn, int fadeOut) {
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
    }

    @Override
    public int getFadeIn() {
        return this.fadeIn;
    }

    @Override
    public int getFadeOut() {
        return this.fadeOut;
    }

    @Override
    public double getFadeInPercent() {
        if(fadeIn == 0) return 1.0;
        return Math.clamp((double) fadeInTicks / getFadeIn(), 0.0, 1.0);
    }

    @Override
    public double getFadeOutPercent() {
        if(fadeOut == 0) return 1.0;
        return Math.clamp((double) fadeOutTicks / getFadeOut(), 0.0, 1.0);
    }

    @Override
    public AnimationState getState() {
        return state;
    }

    @Override
    public AnimationData getAnimation() {
        return animation;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public int animationTime() {
        return (int) (animation.length() * 20);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public AnimationHandler.AnimationDirection direction() {
        return direction;
    }

    @Override
    public void setDirection(AnimationHandler.AnimationDirection direction) {
        this.direction = direction;
    }

    @Override
    public boolean isPlaying() {
        return !paused;
    }

    @Override
    public int getTick() {
        return tick;
    }



}
