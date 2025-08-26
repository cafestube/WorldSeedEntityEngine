package net.worldseed.multipart.model_bones.misc;

import net.kyori.adventure.util.RGBLike;
import net.worldseed.multipart.AbstractGenericModel;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.model_bones.AbstractModelBone;
import net.worldseed.multipart.model_bones.AbstractModelBoneImpl;
import net.worldseed.multipart.model_bones.bone_types.VFXBone;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModelBoneVFX<TViewer, TModel extends AbstractGenericModel<TViewer, TBone, TModel>, TBone extends AbstractModelBone<TViewer, TModel, TBone>> extends AbstractModelBoneImpl<TViewer, TModel, TBone> implements VFXBone<TViewer, TBone, TModel> {
    private final List<TModel> attached = new ArrayList<>();
    private Pos position = Pos.ZERO;

    public ModelBoneVFX(Point pivot, String name, Point rotation, TModel model, float scale) {
        super(pivot, name, rotation, model, scale);
        this.stand = null;
    }

    @Override
    public void attachModel(TModel model) {
        attached.add(model);
    }

    @Override
    public List<TModel> getAttachedModels() {
        return attached;
    }

    @Override
    public void detachModel(TModel model) {
        attached.remove(model);
    }

    @Override
    public void setGlobalRotation(double yaw, double pitch) {

    }

    public Point getPosition() {
        return position;
    }

    @Override
    public void setState(String state) {
    }

    @Override
    public Pos calculatePosition() {
        if (this.offset == null) return Pos.ZERO;

        Point p = this.offset;
        p = applyTransform(p);
        p = calculateGlobalRotation(p);

        Pos endPos = Pos.fromPoint(p);

        return endPos
                .div(4, 4, 4).mul(scale)
                .add(model.getPosition());
    }

    @Override
    public Point calculateRotation() {
        return Vec.ZERO;
    }

    @Override
    public Point calculateScale() {
        return Vec.ZERO;
    }

    public void draw() {
        this.children.forEach(TBone::draw);
        if (this.offset == null) return;

        this.position = calculatePosition();

        this.attached.forEach(model -> {
            model.setPosition(this.position);
            model.setGlobalRotation(this.model.getGlobalRotation());
            model.draw();
        });
    }

    @Override
    public void destroy() {
    }

    @Override
    public void addViewer(TViewer player) {
        this.attached.forEach(model -> model.addViewer(player));
    }

    @Override
    public void removeViewer(TViewer player) {
        this.attached.forEach(model -> model.removeViewer(player));
    }

    @Override
    public void removeGlowing() {
        this.attached.forEach(TModel::removeGlowing);
    }

    @Override
    public void setGlowing(RGBLike color) {
        this.attached.forEach(model -> model.setGlowing(color));
    }

    @Override
    public void removeGlowing(TViewer player) {
        this.attached.forEach(model -> model.removeGlowing(player));
    }

    @Override
    public void setGlowing(TViewer player, RGBLike color) {
        this.attached.forEach(model -> model.setGlowing(player, color));
    }

}
