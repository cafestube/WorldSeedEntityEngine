package net.worldseed.multipart;

import net.minestom.server.entity.Player;
import net.worldseed.multipart.entity.MinestomHitboxBoneEntity;
import net.worldseed.multipart.entity.entity.*;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.entity.MinestomBoneEntity;
import net.worldseed.multipart.entity.MinestomItemDisplayBoneEntity;
import net.worldseed.multipart.entity.MinestomTextDisplayBoneEntity;
import net.worldseed.multipart.entity.display_entity.MinestomRootBoneEntity;
import net.worldseed.multipart.scheduling.Scheduler;

public class MinestomModelPlatform implements ModelPlatform<Player> {

    public static MinestomModelPlatform INSTANCE = new MinestomModelPlatform();

    @Override
    public ItemDisplayBoneEntity<Player> createItemDisplayBoneEntity(GenericModel<Player> model, String name) {
        return new MinestomItemDisplayBoneEntity((MinestomModel) model, name);
    }

    @Override
    public RootBoneEntity<Player> createRootEntity(GenericModel<Player> model) {
        return new MinestomRootBoneEntity((MinestomModel) model);
    }

    @Override
    public TextDisplayBoneEntity<Player> createTextDisplayBoneEntity(GenericModel<Player> model, String name) {
        return new MinestomTextDisplayBoneEntity((MinestomModel) model, name);
    }

    @Override
    public HitboxEntity<Player> createHitboxEntity(GenericModel<Player> model, String name) {
        return new MinestomHitboxBoneEntity((MinestomModel) model, name);
    }

    @Override
    public void spawn(GenericModel<Player> model, BoneEntity<Player> entity, Pos position) {
        if(entity instanceof MinestomBoneEntity boneEntity && model instanceof MinestomModel minestomModel) {
            boneEntity.setInstance(minestomModel.getInstance(), position).join();
        }
    }

    @Override
    public Scheduler getScheduler(GenericModel<Player> model) {
        return new MinestomScheduler();
    }
}
