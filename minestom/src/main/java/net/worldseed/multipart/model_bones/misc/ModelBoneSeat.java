package net.worldseed.multipart.model_bones.misc;

import net.kyori.adventure.util.RGBLike;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.tag.Tag;
import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Quaternion;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.model_bones.ModelBoneImpl;
import net.worldseed.multipart.model_bones.BoneEntity;
import net.worldseed.multipart.model_bones.ModelBone;
import net.worldseed.multipart.model_bones.bone_types.RideableBone;

import java.util.List;

public class ModelBoneSeat extends ModelBoneImpl<Player, MinestomModel> implements RideableBone<Player, MinestomModel> {

    public ModelBoneSeat(Point pivot, String name, Point rotation, MinestomModel model, float scale) {
        super(pivot, name, rotation, model, scale);

        if (this.offset != null) {
            this.stand = new BoneEntity(EntityType.ARMOR_STAND, model, name);

            BoneEntity entity = this.getEntity();
            entity.editEntityMeta(ArmorStandMeta.class, meta -> {
                meta.setMarker(true);
            });

            entity.setTag(Tag.String("WSEE"), "seat");
            entity.setInvisible(true);
        }
    }

    @Override
    public BoneEntity getEntity() {
        return (BoneEntity) super.getEntity();
    }

    @Override
    public void addViewer(Player player) {
        BoneEntity entity = this.getEntity();
        if (entity != null) entity.addViewer(player);
    }

    @Override
    public void removeViewer(Player player) {
        BoneEntity entity = this.getEntity();
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
    public void attachModel(MinestomModel model) {
        throw new UnsupportedOperationException("Cannot attach a model to a seat");
    }

    @Override
    public List<MinestomModel> getAttachedModels() {
        return List.of();
    }

    @Override
    public void detachModel(MinestomModel model) {
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
        BoneEntity entity = this.getEntity();
        
        // TODO: needed by minestom?
        entity.setView(found.yaw(), found.pitch());
        entity.teleport(PositionConversion.asMinestom(found));
    }

//    @Override
//    public void addPassenger(Entity entity) {
//        entity.addPassenger(entity);
//    }
//
//    @Override
//    public void removePassenger(Entity entity) {
//        entity.removePassenger(entity);
//    }
//
//    @Override
//    public Set<Entity> getPassengers() {
//        return entity.getPassengers();
//    }
}
