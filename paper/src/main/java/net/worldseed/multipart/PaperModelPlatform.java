package net.worldseed.multipart;

import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.entity.*;
import net.worldseed.multipart.entity.entity.*;
import net.worldseed.multipart.scheduling.Scheduler;
import org.bukkit.entity.Player;

public class PaperModelPlatform implements ModelPlatform<Player> {

    public static PaperModelPlatform INSTANCE = new PaperModelPlatform();

    @Override
    public ItemDisplayBoneEntity<Player> createItemDisplayBoneEntity(GenericModel<Player> model, String name) {
        return new PaperItemDisplayBoneEntity((PaperModel) model, name);
    }

    @Override
    public RootBoneEntity<Player> createRootEntity(GenericModel<Player> model) {
        return new PaperRootBoneEntity((PaperModel) model);
    }

    @Override
    public TextDisplayBoneEntity<Player> createTextDisplayBoneEntity(GenericModel<Player> model, String name) {
        return new PaperTextDisplayBoneEntity((PaperModel) model, name);
    }

    @Override
    public HitboxEntity<Player> createHitboxEntity(GenericModel<Player> model, String name) {
        return new PaperHitboxEntity((PaperModel) model, name);
    }

    @Override
    public void spawn(GenericModel<Player> model, BoneEntity<Player> entity, Pos position) {
        if(entity instanceof PaperHitboxEntity hitbox) {
            hitbox.spawnAt(position);
            return;
        }
        entity.teleport(position);
    }

    @Override
    public Scheduler getScheduler(GenericModel<Player> model) {
        return new PaperScheduler(((PaperModel) model).getPlugin());
    }
}
