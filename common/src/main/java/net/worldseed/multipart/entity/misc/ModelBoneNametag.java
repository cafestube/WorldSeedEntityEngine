package net.worldseed.multipart.entity.misc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.RGBLike;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.entity.entity.TextDisplayBoneEntity;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.entity.ModelBoneImpl;
import net.worldseed.multipart.entity.bone_types.NametagBone;

import java.util.List;

public class ModelBoneNametag<TViewer> extends ModelBoneImpl<TViewer> implements NametagBone<TViewer> {
    public ModelBoneNametag(Point pivot, String name, Point rotation, GenericModel<TViewer> model, float scale) {
        super(pivot, name, rotation, model, scale);
    }

    @Override
    public TextDisplayBoneEntity<TViewer> getEntity() {
        return (TextDisplayBoneEntity<TViewer>) super.getEntity();
    }

    @Override
    public void addViewer(TViewer player) {
        TextDisplayBoneEntity<TViewer> entity = this.getEntity();
        if (entity != null) entity.addViewer(player);
    }

    @Override
    public void removeViewer(TViewer player) {
        TextDisplayBoneEntity<TViewer> entity = this.getEntity();
        if (entity != null) entity.removeViewer(player);
    }

    @Override
    public void removeGlowing() {
    }

    @Override
    public void setGlowing(RGBLike color) {
    }

    @Override
    public void removeGlowing(TViewer player) {
    }

    @Override
    public void setGlowing(TViewer player, RGBLike color) {
    }

    @Override
    public void attachModel(GenericModel<TViewer> model) {
        throw new UnsupportedOperationException("Cannot attach a model to a nametag");
    }

    @Override
    public List<GenericModel<TViewer>> getAttachedModels() {
        return List.of();
    }

    @Override
    public void detachModel(GenericModel<TViewer> model) {
        throw new UnsupportedOperationException("Cannot detach a model from a nametag");
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

    public void draw() {
        TextDisplayBoneEntity<TViewer> entity = this.getEntity();
        if (this.offset == null || stand == null) return;

        entity.setTranslation(calculatePositionInternal());
    }

    @Override
    public Pos calculatePosition() {
        if (stand == null) return Pos.ZERO;
        if (this.offset == null) return Pos.ZERO;

        Point p = this.offset;
        p = applyTransform(p);
        p = calculateGlobalRotation(p);

        return Pos.fromPoint(p)
                .div(4, 4, 4).mul(scale)
                .add(model.getPosition())
                .add(model.getGlobalOffset());
    }

    @Override
    public Point calculateRotation() {
        return Vec.ZERO;
    }

    @Override
    public Point calculateScale() {
        return Vec.ZERO;
    }

    private Pos calculatePositionInternal() {
        if (this.offset == null) return Pos.ZERO;
        Point p = this.offset;
        p = applyTransform(p);
        return new Pos(p).div(4).mul(scale).withView(0, 0);
    }

    @Override
    public void setNametag(Component component) {
        if(component == null && this.stand == null) return;

        if(component == null) {
            this.stand.remove();
            this.stand = null;
            return;
        }

        TextDisplayBoneEntity<TViewer> entity = this.getEntity();
        if(entity != null && (!entity.isRemoved() || model.isSpawned())) {
            entity.setText(component);
            return;
        }

        this.stand = entity = model.getModelPlatform().createTextDisplayBoneEntity(model, this.name);

        entity.setTranslation(calculatePositionInternal());
        entity.setText(component);

        if(model.isSpawned()) {
            model.getModelPlatform().spawn(model, entity, model.getPosition());
            model.getModelRoot().attachEntity(entity);
        }
    }

    @Override
    public Component getNametag() {
        TextDisplayBoneEntity<TViewer> entity = this.getEntity();
        if(entity == null) return null;
        return entity.getText();
    }
}
