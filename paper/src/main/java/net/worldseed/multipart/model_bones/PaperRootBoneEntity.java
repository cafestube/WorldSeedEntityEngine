package net.worldseed.multipart.model_bones;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.model_bones.entity.BoneEntity;
import net.worldseed.multipart.model_bones.entity.RootBoneEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PaperRootBoneEntity extends PaperPacketBoneEntity implements RootBoneEntity<Player> {

    private final List<BoneEntity<Player>> attachedEntities = new ArrayList<>();

    public PaperRootBoneEntity(PaperModel model) {
        super(EntityType.ITEM_DISPLAY, model, "root");
    }

    @Override
    public void attachEntity(BoneEntity<Player> entity) {
        if (!this.attachedEntities.contains(entity)) {
            this.attachedEntities.add(entity);
        }
    }

    @Override
    protected List<Packet<? super ClientGamePacketListener>> getInitialPackets() {
        List<Packet<? super ClientGamePacketListener>> packets = super.getInitialPackets();

        packets.add(ClientboundSetPassengersPacket.STREAM_CODEC.decode(new FriendlyByteBuf(null) {
            @Override
            public int readVarInt() {
                return entityId;
            }

            @Override
            public int @NotNull [] readVarIntArray() {
                return attachedEntities.stream().mapToInt(BoneEntity::getEntityId).toArray();
            }
        }));

        return packets;
    }
}
