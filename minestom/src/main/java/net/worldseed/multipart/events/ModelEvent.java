package net.worldseed.multipart.events;

import net.minestom.server.event.Event;
import net.worldseed.multipart.MinestomModel;

public interface ModelEvent extends Event {
    MinestomModel model();
}
