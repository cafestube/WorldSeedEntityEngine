package net.worldseed.multipart;

import net.minestom.server.collision.Shape;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventHandler;
import net.minestom.server.instance.Instance;
import net.worldseed.multipart.events.ModelEvent;
import net.worldseed.multipart.model_bones.BoneEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface MinestomModel extends GenericModel<Player>, Shape, EventHandler<@NotNull ModelEvent> {

    @Override
    ModelRegistry getModelRegistry();

    void mountEntity(String name, Entity entity);

    void dismountEntity(String name, Entity entity);

    Collection<Entity> getPassengers(String name);

    Instance getInstance();

    @Override
    BoneEntity getModelRoot();
}
