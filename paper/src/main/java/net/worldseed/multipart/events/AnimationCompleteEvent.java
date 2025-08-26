package net.worldseed.multipart.events;

import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.animations.AnimationHandler;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class AnimationCompleteEvent extends ModelEvent {

    private final String animation;
    private final AnimationHandler.AnimationDirection direction;
    private static final HandlerList HANDLERS = new HandlerList();

    public AnimationCompleteEvent(@NotNull PaperModel model, String animation, AnimationHandler.AnimationDirection direction) {
        super(model);
        this.animation = animation;
        this.direction = direction;
    }

    public String animation() {
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
