package net.worldseed.multipart.entity.display_entity;

import net.kyori.adventure.util.RGBLike;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Quaternion;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.entity.*;
import net.worldseed.multipart.entity.entity.BoneEntity;
import net.worldseed.multipart.entity.entity.ItemDisplayBoneEntity;

import java.util.ArrayList;
import java.util.List;

public class ModelBonePartDisplay<TViewer> extends ModelBoneImpl<TViewer> implements ModelBoneViewable {
    private final List<GenericModel<TViewer>> attached = new ArrayList<>();

    public ModelBonePartDisplay(Point pivot, String name, Point rotation, GenericModel<TViewer> model, float scale) {
        super(pivot, name, rotation, model, scale);

        if (this.offset != null) {
            ItemDisplayBoneEntity<TViewer> entity = model.getModelPlatform().createItemDisplayBoneEntity(model, name);
            this.stand = entity;

            entity.setScale(new Vec(scale, scale, scale));
            entity.setFixedContext();
            entity.setTransformationInterpolationDuration(2);
            entity.setPosRotInterpolationDuration(2);
            entity.setViewRange(1000);
        }
    }

    @Override
    public void addViewer(TViewer player) {
        BoneEntity<TViewer> entity = this.getEntity();
        if (entity != null) entity.addViewer(player);
        this.attached.forEach(model -> model.addViewer(player));
    }

    @Override
    public void removeGlowing() {
        BoneEntity<TViewer> entity = this.getEntity();
        if (entity != null) {
            entity.setGlowing(false);
        }

        this.attached.forEach(GenericModel::removeGlowing);
    }

    @Override
    public void setGlowing(RGBLike color) {
        BoneEntity<TViewer> entity = this.getEntity();
        if (entity instanceof ItemDisplayBoneEntity<TViewer> display) {
            display.setGlowing(color);
        }

        this.attached.forEach(model -> model.setGlowing(color));
    }

    @Override
    public void removeGlowing(TViewer player) {
        BoneEntity<TViewer> entity = this.getEntity();
        if (entity == null)
            return;

        entity.setGlowing(player, false);

        this.attached.forEach(model -> model.removeGlowing(player));
    }

    @Override
    public void setGlowing(TViewer player, RGBLike color) {
        BoneEntity<TViewer> entity = this.getEntity();
        if (!(entity instanceof ItemDisplayBoneEntity<TViewer> display))
            return;

        display.setGlowing(player, color);

        this.attached.forEach(model -> model.setGlowing(player, color));
    }

    @Override
    public void attachModel(GenericModel<TViewer> model) {
        attached.add(model);
    }

    @Override
    public List<GenericModel<TViewer>> getAttachedModels() {
        return attached;
    }

    @Override
    public void detachModel(GenericModel<TViewer> model) {
        attached.remove(model);
    }

    @Override
    public void setGlobalRotation(double yaw, double pitch) {
        BoneEntity<TViewer> entity = this.getEntity();
        if (entity != null) {
            var correctYaw = (180 + yaw + 360) % 360;
            var correctPitch = (pitch + 360) % 360;
            entity.setRotation((float) correctYaw, (float) correctPitch);
        }
    }

    @Override
    public void removeViewer(TViewer player) {
        BoneEntity<TViewer> entity = this.getEntity();
        if (entity != null) entity.removeViewer(player);
        this.attached.forEach(model -> model.removeViewer(player));
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public Pos calculatePosition() {
        return new Pos(model.getPosition()).withView((float) (180 + this.model.getGlobalRotation() + 360) % 360, 0);
    }

    private Pos calculatePositionInternal() {
        if (this.offset == null) return Pos.ZERO;
        Point p = this.offset;
        p = applyTransform(p);
        return new Pos(p).div(4).mul(scale).withView(0, 0);
    }

    @Override
    public Point calculateRotation() {
        Quaternion q = calculateFinalAngle(new Quaternion(getPropogatedRotation()));
        return q.toEuler();
    }

    @Override
    public Point calculateScale() {
        return calculateFinalScale(getPropogatedScale());
    }

    public void draw() {
        this.children.forEach(ModelBone::draw);
        if (this.offset == null) return;

        BoneEntity<TViewer> entity = this.getEntity();
        if (entity instanceof ItemDisplayBoneEntity<TViewer> display) {
            var position = calculatePositionInternal();
            var scale = calculateScale();

            Quaternion q = calculateFinalAngle(new Quaternion(getPropogatedRotation()));

//                meta.setNotifyAboutChanges(false);
            //TODO: Notify changes?

            display.setTransformationInterpolationStartDelta(0);
            display.setScale(new Vec(scale.x() * this.scale, scale.y() * this.scale, scale.z() * this.scale));
            display.setRightRotation(q);
            display.setTranslation(position);
//                meta.setNotifyAboutChanges(true);

            attached.forEach(model -> {
                model.setPosition(this.model.getPosition().add(calculateGlobalRotation(position)));
                model.setGlobalRotation(-q.toEuler().x() + this.model.getGlobalRotation());
                model.draw();
            });
        }
    }

    @Override
    public void setState(String state) {
        BoneEntity<TViewer> entity = this.getEntity();
        if (entity instanceof ItemDisplayBoneEntity<TViewer> display) {
            if (state.equals("invisible")) {
                display.clearItem();
                return;
            }

            display.setItemState(state);
        }
    }


    @Override
    public Point getPosition() {
        return calculatePositionInternal().add(model.getPosition());
    }
}
