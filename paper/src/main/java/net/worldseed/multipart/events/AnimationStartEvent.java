package net.worldseed.multipart.events;

import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.animations.ModelAnimation;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class AnimationStartEvent extends ModelEvent {

    private final ModelAnimation animation;
    private final AnimationHandler.AnimationDirection direction;
    private final short tick;
    private final boolean looped;
    private static final HandlerList HANDLERS = new HandlerList();

    public AnimationStartEvent(@NotNull PaperModel model, ModelAnimation animation, AnimationHandler.AnimationDirection direction, short tick, boolean looped) {
        super(model);
        this.animation = animation;
        this.direction = direction;
        this.tick = tick;
        this.looped = looped;
    }

    public short tick() {
        return tick;
    }

    public boolean looped() {
        return looped;
    }

    public ModelAnimation animation() {
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
