package net.worldseed.multipart.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

public class NMSHitboxEntity extends Interaction {

    public NMSHitboxEntity(World level) {
        super(EntityType.INTERACTION, ((CraftWorld) level).getHandle());
        persist = false;
    }



}
