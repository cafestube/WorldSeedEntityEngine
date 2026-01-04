package net.worldseed.multipart.events;

import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.animations.ModelAnimation;

public record AnimationStoppedEvent(
        MinestomModel model, ModelAnimation animation,
        AnimationHandler.AnimationDirection direction,
        boolean looped
) implements ModelEvent {
}
