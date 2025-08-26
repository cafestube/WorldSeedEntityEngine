package net.worldseed.multipart.events;

import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.animations.AnimationHandler;
import org.jetbrains.annotations.NotNull;

public record AnimationCompleteEvent(MinestomModel model, String animation,
                                     AnimationHandler.AnimationDirection direction) implements ModelEvent {
    public AnimationCompleteEvent(@NotNull MinestomModel model, String animation, AnimationHandler.AnimationDirection direction) {
        this.animation = animation;
        this.direction = direction;
        this.model = model;
    }
}
