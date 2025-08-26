package net.worldseed.multipart.events;

import net.minestom.server.entity.Entity;
import net.worldseed.multipart.MinestomModel;
import org.jetbrains.annotations.NotNull;

public record ModelDismountEvent(MinestomModel model, Entity rider) implements ModelEvent {
    public ModelDismountEvent(@NotNull MinestomModel model, Entity rider) {
        this.rider = rider;
        this.model = model;
    }

    @Override
    public @NotNull MinestomModel model() {
        return model;
    }

    @Override
    public @NotNull Entity rider() {
        return rider;
    }
}

