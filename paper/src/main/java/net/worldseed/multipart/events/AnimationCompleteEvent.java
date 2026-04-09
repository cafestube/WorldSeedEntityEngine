package net.worldseed.multipart.events;

import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.animations.ModelAnimationInstance;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class AnimationCompleteEvent extends ModelEvent {

    private final ModelAnimationInstance animation;
    private final AnimationHandler.AnimationDirection direction;
    private static final HandlerList HANDLERS = new HandlerList();

    public AnimationCompleteEvent(@NotNull PaperModel model, ModelAnimationInstance animation, AnimationHandler.AnimationDirection direction) {
        super(model);
        this.animation = animation;
        this.direction = direction;
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
