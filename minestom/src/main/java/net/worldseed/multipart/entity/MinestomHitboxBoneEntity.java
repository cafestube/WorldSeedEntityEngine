package net.worldseed.multipart.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.network.packet.server.play.EntityTeleportPacket;
import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.entity.entity.HitboxEntity;
import net.worldseed.multipart.entity.entity.TextDisplayBoneEntity;
import net.worldseed.multipart.math.Pos;
import org.jetbrains.annotations.NotNull;

public class MinestomHitboxBoneEntity extends MinestomBoneEntity implements HitboxEntity<Player> {

    public MinestomHitboxBoneEntity(MinestomModel model, String name) {
        super(EntityType.INTERACTION, model, name);
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        super.updateNewViewer(player);

        EntityTeleportPacket packet = new EntityTeleportPacket(this.getEntityId(), this.position, Vec.ZERO, 0, false);
        player.getPlayerConnection().sendPacket(packet);
    }

    @Override
    public void updateOldViewer(@NotNull Player player) {
        super.updateOldViewer(player);
    }

    @Override
    public void setSize(double sizeX, double sizeY) {
        InteractionMeta meta = (InteractionMeta) this.getEntityMeta();
        if(meta.getWidth() == sizeX && meta.getHeight() == sizeY)
            return;
        meta.setHeight((float) sizeY);
        meta.setWidth((float) sizeX);

        this.setBoundingBox(sizeX, sizeY, sizeX);
    }
}
