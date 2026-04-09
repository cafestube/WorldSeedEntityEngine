package net.worldseed.multipart.events;

import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.animations.ModelAnimationInstance;

public record AnimationCompleteEvent(
        MinestomModel model, ModelAnimationInstance animation,
        AnimationHandler.AnimationDirection direction
) implements ModelEvent {
}
