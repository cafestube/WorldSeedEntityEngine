package net.worldseed.multipart.events;

import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.animations.ModelAnimation;

public record AnimationStartEvent(
        MinestomModel model, ModelAnimation animation,
        AnimationHandler.AnimationDirection direction,
        short tick, boolean looped
) implements ModelEvent {
}
