package net.worldseed.multipart.events;

import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.animations.AnimationHandler;
import org.jetbrains.annotations.NotNull;

public record AnimationCompleteEvent(GenericModel model, String animation,
                                     AnimationHandler.AnimationDirection direction) implements ModelEvent {
    public AnimationCompleteEvent(@NotNull GenericModel model, String animation, AnimationHandler.AnimationDirection direction) {
        this.animation = animation;
        this.direction = direction;
        this.model = model;
    }
}
