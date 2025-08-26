package net.worldseed.multipart;

import net.worldseed.multipart.model_bones.PaperRootBoneEntity;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface PaperModel extends GenericModel<Player> {

    @Override
    ModelRegistry getModelRegistry();

//    void mountEntity(String name, Entity entity);
//
//    void dismountEntity(String name, Entity entity);
//
//    Collection<Entity> getPassengers(String name);

    World getWorld();

    @Override
    PaperRootBoneEntity getModelRoot();
}
