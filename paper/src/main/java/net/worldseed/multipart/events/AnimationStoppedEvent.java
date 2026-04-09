package net.worldseed.multipart.events;

import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.animations.ModelAnimationInstance;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class AnimationStoppedEvent extends ModelEvent {

    private final ModelAnimationInstance animation;
    private final AnimationHandler.AnimationDirection direction;
    private final boolean looped;
    private static final HandlerList HANDLERS = new HandlerList();

    public AnimationStoppedEvent(@NotNull PaperModel model, ModelAnimationInstance animation, AnimationHandler.AnimationDirection direction, boolean looped) {
        super(model);
        this.animation = animation;
        this.direction = direction;
        this.looped = looped;
    }

    public boolean looped() {
        return looped;
    }

    public ModelAnimationInstance animation() {
        return animation;
    }

    public AnimationHandler.AnimationDirection direction() {
        return direction;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
