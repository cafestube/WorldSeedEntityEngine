package net.worldseed.multipart.entity.display_entity;

import net.minestom.server.ServerFlag;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.entity.MinestomBoneEntity;
import net.worldseed.multipart.entity.entity.BoneEntity;
import net.worldseed.multipart.entity.entity.RootBoneEntity;

public class MinestomRootBoneEntity extends MinestomBoneEntity implements RootBoneEntity<Player> {
    public MinestomRootBoneEntity(MinestomModel model) {
        super(EntityType.ITEM_DISPLAY, model, "root");

        this.setInvisible(true);
        this.setNoGravity(true);
        this.setSynchronizationTicks(ServerFlag.ENTITY_SYNCHRONIZATION_TICKS);
    }

    @Override
    public void attachEntity(BoneEntity<Player> entity) {
        if(entity instanceof MinestomBoneEntity boneEntity) {
            addPassenger(boneEntity);
        } else {
            throw new IllegalArgumentException("Entity must be an instance of MinestomBoneEntity to attach to MinestomRootBoneEntity");
        }
    }


}
