package net.worldseed.multipart.events;

import net.minestom.server.network.packet.client.play.ClientInputPacket;
import net.worldseed.multipart.MinestomModel;
import org.jetbrains.annotations.NotNull;

public record ModelControlEvent(MinestomModel model, ClientInputPacket packet) implements ModelEvent {
    public ModelControlEvent(@NotNull MinestomModel model, ClientInputPacket packet) {
        this.model = model;
        this.packet = packet;
    }

    @Override
    public @NotNull MinestomModel model() {
        return model;
    }

    public @NotNull ClientInputPacket packet() {
        return packet;
    }
}

