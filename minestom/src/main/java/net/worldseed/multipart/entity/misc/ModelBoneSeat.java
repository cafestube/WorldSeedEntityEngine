package net.worldseed.multipart.entity.misc;

import net.kyori.adventure.util.RGBLike;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.tag.Tag;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Quaternion;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.entity.ModelBoneImpl;
import net.worldseed.multipart.entity.MinestomBoneEntity;
import net.worldseed.multipart.entity.ModelBone;
import net.worldseed.multipart.entity.bone_types.RideableBone;

import java.util.List;
import java.util.Set;

public class ModelBoneSeat extends ModelBoneImpl<Player> implements RideableBone {

    public ModelBoneSeat(Point pivot, String name, Point rotation, MinestomModel model, float scale) {
        super(pivot, name, rotation, model, scale);

        if (this.offset != null) {
            this.stand = new MinestomBoneEntity(EntityType.ARMOR_STAND, model, name);

            MinestomBoneEntity entity = this.getEntity();
            entity.editEntityMeta(ArmorStandMeta.class, meta -> {
                meta.setMarker(true);
            });

            entity.setTag(Tag.String("WSEE"), "seat");
            entity.setInvisible(true);
        }
    }

    @Override
    public MinestomBoneEntity getEntity() {
        return (MinestomBoneEntity) super.getEntity();
    }

    @Override
    public void addViewer(Player player) {
        MinestomBoneEntity entity = this.getEntity();
        if (entity != null) entity.addViewer(player);
    }

    @Override
    public void removeViewer(Player player) {
        MinestomBoneEntity entity = this.getEntity();
        if (entity != null) entity.removeViewer(player);
    }

    @Override
    public void removeGlowing() {

    }

    @Override
    public void setGlowing(RGBLike color) {

    }

    @Override
    public void removeGlowing(Player player) {

    }

    @Override
    public void setGlowing(Player player, RGBLike color) {

    }

    @Override
    public void attachModel(GenericModel<Player> model) {
        throw new UnsupportedOperationException("Cannot attach a model to a seat");
    }

    @Override
    public List<GenericModel<Player>> getAttachedModels() {
        return List.of();
    }

    @Override
    public void detachModel(GenericModel<Player> model) {
        throw new UnsupportedOperationException("Cannot detach a model from a seat");
    }

    @Override
    public void setGlobalRotation(double yaw, double pitch) {

    }

    @Override
    public void setState(String state) {
    }

    @Override
    public Point getPosition() {
        return calculatePosition();
    }

    @Override
    public Pos calculatePosition() {
        if (this.offset == null) return Pos.ZERO;

        var rotation = calculateRotation();

        var p = applyTransform(this.offset);
        p = calculateGlobalRotation(p);
        Pos endPos = Pos.fromPoint(p);

        return endPos
                .div(4, 4, 4).mul(scale)
                .add(model.getPosition())
                .add(model.getGlobalOffset())
                .withView((float) -rotation.y(), (float) rotation.x());
    }

    @Override
    public Point calculateRotation() {
        Quaternion q = new Quaternion(new Vec(0, 180 - this.model.getGlobalRotation(), 0));
        return q.toEulerYZX();
    }

    @Override
    public Point calculateScale() {
        return Vec.ZERO;
    }

    public void draw() {
        this.children.forEach(ModelBone::draw);
        if (this.offset == null) return;

        Pos found = calculatePosition();
        MinestomBoneEntity entity = this.getEntity();
        
        // TODO: needed by minestom?
        entity.setView(found.yaw(), found.pitch());
        entity.teleport(PositionConversion.asMinestom(found));
    }

    @Override
    public void addPassenger(Entity entity) {
        this.getEntity().addPassenger(entity);
    }

    @Override
    public void removePassenger(Entity entity) {
        this.getEntity().removePassenger(entity);
    }

    @Override
    public Set<Entity> getPassengers() {
        return this.getEntity().getPassengers();
    }
}
