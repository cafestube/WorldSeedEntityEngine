package net.worldseed.multipart.model_bones.display_entity;

import net.minestom.server.ServerFlag;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.network.packet.server.play.SetPassengersPacket;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.model_bones.BoneEntity;
import net.worldseed.multipart.model_bones.ModelBone;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RootBoneEntity extends BoneEntity {
    public RootBoneEntity(GenericModel model) {
        super(EntityType.ITEM_DISPLAY, model, "root");

        this.setInvisible(true);
        this.setNoGravity(true);
        this.setSynchronizationTicks(ServerFlag.ENTITY_SYNCHRONIZATION_TICKS);
    }
}
