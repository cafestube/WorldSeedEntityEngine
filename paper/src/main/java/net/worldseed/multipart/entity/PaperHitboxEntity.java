package net.worldseed.multipart.entity;

import net.minecraft.world.entity.Entity;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.entity.entity.HitboxEntity;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class PaperHitboxEntity implements HitboxEntity<Player> {

    private final NMSHitboxEntity entity;
    private final PaperModel model;

    public PaperHitboxEntity(PaperModel model, String name) {
        this.entity = new NMSHitboxEntity(model.getWorld());
        this.model = model;
    }

    public void spawnAt(Pos position) {
        entity.setPos(position.x(), position.y(), position.z());
        entity.setRot(position.yaw(), position.pitch());
//        entity.visibleByDefault = false;
        entity.level().addFreshEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public void setSize(double width, double height) {
        this.entity.setWidth((float) width);
        this.entity.setHeight((float) height);
    }

    @Override
    public int getEntityId() {
        return this.entity.getId();
    }

    @Override
    public void remove() {
        this.entity.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    public boolean addViewer(Player player) {
        player.showEntity(model.getPlugin(), this.entity.getBukkitEntity());
        return true;
    }

    @Override
    public boolean removeViewer(Player player) {
        player.hideEntity(model.getPlugin(), this.entity.getBukkitEntity());
        return true;
    }

    @Override
    public void setGlowing(boolean b) {}

    @Override
    public void setGlowing(Player player, boolean b) {}

    @Override
    public void setRotation(float yaw, float pitch) {}

    @Override
    public void teleport(Pos pos) {
        this.entity.getBukkitEntity().teleport(PositionConversion.asPaper(model.getWorld(), pos));
    }

    @Override
    public Pos getLocation() {
        return new Pos(entity.getX(), entity.getY(), entity.getZ(), 0, 0);
    }

    @Override
    public boolean isRemoved() {
        return entity.isRemoved();
    }

    @Override
    public double getDistanceSquared(Pos newPos) {
        return entity.getBukkitEntity().getLocation().distanceSquared(PositionConversion.asPaper(this.model.getWorld(), newPos));
    }
}
