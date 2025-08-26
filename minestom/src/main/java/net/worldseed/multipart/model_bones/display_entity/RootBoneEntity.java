package net.worldseed.multipart.model_bones.display_entity;

import net.minestom.server.ServerFlag;
import net.minestom.server.entity.EntityType;
import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.model_bones.BoneEntity;

public class RootBoneEntity extends BoneEntity {
    public RootBoneEntity(MinestomModel model) {
        super(EntityType.ITEM_DISPLAY, model, "root");

        this.setInvisible(true);
        this.setNoGravity(true);
        this.setSynchronizationTicks(ServerFlag.ENTITY_SYNCHRONIZATION_TICKS);
    }
}
