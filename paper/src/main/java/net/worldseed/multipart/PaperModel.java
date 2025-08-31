package net.worldseed.multipart;

import net.worldseed.multipart.entity.PaperRootBoneEntity;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface PaperModel extends GenericModel<Player> {

    @Override
    ModelRegistry getModelRegistry();

//    void mountEntity(String name, Entity entity);
//
//    void dismountEntity(String name, Entity entity);
//
//    Collection<Entity> getPassengers(String name);

    World getWorld();

    JavaPlugin getPlugin();

    @Override
    PaperRootBoneEntity getModelRoot();
}
