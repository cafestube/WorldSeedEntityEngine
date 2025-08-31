package net.worldseed.multipart.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.worldseed.multipart.PaperModel;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

public class NMSHitboxEntity extends Interaction {

    private final PaperModel model;

    public NMSHitboxEntity(World level, PaperModel model) {
        super(EntityType.INTERACTION, ((CraftWorld) level).getHandle());
        this.model = model;
        persist = false;
    }

    public PaperModel getModel() {
        return model;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return true;
    }
}
