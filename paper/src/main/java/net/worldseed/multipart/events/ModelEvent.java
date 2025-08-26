package net.worldseed.multipart.events;

import net.worldseed.multipart.PaperModel;
import org.bukkit.event.Event;

public abstract class ModelEvent extends Event {

    private final PaperModel model;

    public ModelEvent(PaperModel model) {
        this.model = model;
    }

    public PaperModel model() {
        return this.model;
    }

}
