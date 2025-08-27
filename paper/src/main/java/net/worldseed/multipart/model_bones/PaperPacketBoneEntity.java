package net.worldseed.multipart.model_bones;

import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.model_bones.entity.BoneEntity;
import net.worldseed.multipart.util.DataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class PaperPacketBoneEntity implements BoneEntity<Player> {
    private final PaperModel model;
    private final String name;

    private final List<Player> viewers = new ArrayList<>();
    private final EntityType entityType;
    public final int entityId;

    protected DataWatcher dataWatcher = new DataWatcher();
    private final UUID uuid;
    private Pos pos = new Pos(0, 0, 0, 0, 0);

    public PaperPacketBoneEntity(@NotNull EntityType entityType, PaperModel model, String name) {
        this.model = model;
        this.name = name;

        this.entityType = entityType;
        this.entityId = Bukkit.getUnsafe().nextEntityId();
        this.uuid = UUID.randomUUID();
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    public PaperModel getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    @Override
    public void remove() {
        sendPacketToViewers(new ClientboundRemoveEntitiesPacket(entityId));
        viewers.clear();
    }

    protected void sendPacketToViewers(Packet<?> packet) {
        for (Player player : viewers) {
            sendPacket(player, packet);
        }
    }

    protected void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public boolean addViewer(Player player) {
        if(viewers.contains(player)) {
            return false;
        }

        updateNewViewer(player);
        return true;
    }

    private void updateNewViewer(Player player) {
        sendPacket(player, new ClientboundBundlePacket(
            getInitialPackets()
        ));
    }

    protected List<Packet<? super ClientGamePacketListener>> getInitialPackets() {
        ArrayList<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>();

        packets.add(new ClientboundAddEntityPacket(this.entityId, this.uuid, this.pos.x(), this.pos.y(), this.pos.z(),
                0f, this.pos.yaw(), CraftEntityType.bukkitToMinecraft(this.entityType),
                0, Vec3.ZERO, this.pos.yaw()));
        packets.add(new ClientboundSetEntityDataPacket(this.entityId, dataWatcher.packAll()));
        return packets;
    }

    @Override
    public boolean removeViewer(Player player) {
        if(viewers.contains(player)) {
            viewers.remove(player);

            sendPacket(player, new ClientboundRemoveEntitiesPacket(entityId));

            return true;
        }

        return false;
    }

    @Override
    public void setGlowing(boolean b) {

    }

    @Override
    public void setGlowing(Player player, boolean b) {

    }

    @Override
    public void setRotation(float yaw, float pitch) {
        this.pos = this.pos.withView(yaw, pitch);

        ClientboundMoveEntityPacket moveEntityPacket = new ClientboundMoveEntityPacket.Rot(entityId, (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true);
        sendPacketToViewers(moveEntityPacket);
    }

    @Override
    public void teleport(Pos pos) {
        this.pos = pos;

        PositionMoveRotation positionMoveRotation = new PositionMoveRotation(new Vec3(pos.x(), pos.y(), pos.z()), Vec3.ZERO, pos.yaw(), pos.pitch());
        ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(entityId, positionMoveRotation, new HashSet<>(), false);
        sendPacketToViewers(teleportEntityPacket);
    }
}
