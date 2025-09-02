package net.worldseed.multipart.entity;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.LazyPacket;
import net.minestom.server.network.packet.server.play.SpawnEntityPacket;
import net.minestom.server.tag.Tag;
import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.entity.entity.BoneEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MinestomBoneEntity extends LivingEntity implements BoneEntity<Player> {
    private final MinestomModel model;
    private final String name;

    public MinestomBoneEntity(@NotNull EntityType entityType, MinestomModel model, String name) {
        super(entityType);
        this.setAutoViewable(false);
        setTag(Tag.String("WSEE"), "part");
        this.model = model;
        this.name = name;

        this.setSilent(true);
        this.setNoGravity(true);
        this.setSynchronizationTicks(Integer.MAX_VALUE);
    }

    @Override
    public @NotNull Set<Player> getViewers() {
        return model.getViewers();
    }

    public MinestomModel getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    @Override
    public void tick(long time) {
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        Pos position = this.getPosition();
        var spawnPacket = new SpawnEntityPacket(this.getEntityId(), this.getUuid(), this.getEntityType().id(), PositionConversion.asMinestom(model.getPosition().withView(position.yaw(), 0)), position.yaw(), 0, (short) 0, (short) 0, (short) 0);

        player.sendPacket(spawnPacket);
        player.sendPacket(new LazyPacket(this::getMetadataPacket));

        if (this.getEntityType() == EntityType.ZOMBIE || this.getEntityType() == EntityType.ARMOR_STAND)
            player.sendPacket(getEquipmentsPacket());

        if (!getPassengers().isEmpty()) {
            player.sendPacket(getPassengersPacket());
        }
    }

    public CompletableFuture<Void> setInstance(Instance instance, net.worldseed.multipart.math.Point position) {
        return super.setInstance(instance, PositionConversion.asMinestom(position));
    }

    @Override
    public void setGlowing(Player player, boolean b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        setView(yaw, pitch);
    }

    @Override
    public void teleport(net.worldseed.multipart.math.Pos pos) {
        super.teleport(PositionConversion.asMinestom(pos));
    }

    @Override
    public net.worldseed.multipart.math.Pos getLocation() {
        return PositionConversion.fromMinestom(this.getPosition());
    }

    @Override
    public double getDistanceSquared(net.worldseed.multipart.math.Pos newPos) {
        return this.getPosition().distanceSquared(PositionConversion.asMinestom(newPos));
    }

    @Override
    public void setNotifyAboutChanges(boolean b) {
        this.metadata.setNotifyAboutChanges(b);
    }
}
