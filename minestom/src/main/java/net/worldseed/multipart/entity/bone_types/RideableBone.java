package net.worldseed.multipart.entity.bone_types;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.worldseed.multipart.entity.ModelBone;

import java.util.Set;

public interface RideableBone extends ModelBone<Player> {

    void addPassenger(Entity entity);

    void removePassenger(Entity entity);

    Set<Entity> getPassengers();

}
