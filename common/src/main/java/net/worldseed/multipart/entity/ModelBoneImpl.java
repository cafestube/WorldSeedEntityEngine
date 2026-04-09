package net.worldseed.multipart.entity;

import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.animations.AnimationLoader;
import net.worldseed.multipart.animations.BoneAnimationTransform;
import net.worldseed.multipart.math.*;
import net.worldseed.multipart.entity.entity.BoneEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class ModelBoneImpl<TViewer> implements ModelBone<TViewer> {
    protected final Point pivot;
    protected final String name;
    protected final ArrayList<ModelBone<TViewer>> children = new ArrayList<>();
    protected final GenericModel<TViewer> model;
    protected Point diff;
    protected float scale;
    protected Point offset;
    protected Point rotation;
    protected BoneEntity<TViewer> stand;
    private ModelBone<TViewer> parent;
    private BoneAnimationTransform animationTransform = BoneAnimationTransform.ZERO;

    public ModelBoneImpl(Point pivot, String name, Point rotation, Point diff, Point offset, GenericModel<TViewer> model, float scale) {
        this.name = name;
        this.rotation = rotation;
        this.model = model;

        this.diff = diff;
        this.offset = offset;

        if (this.diff != null) this.pivot = pivot.add(this.diff);
        else this.pivot = pivot;

        this.scale = scale;
    }

    @Override
    public void draw() {
        model.getAnimationHandler().updateBone(this);
    }

    @Override
    public @NotNull BoneAnimationTransform getAnimationTransform() {
        return this.animationTransform;
    }

    @Override
    public void setAnimationTransform(BoneAnimationTransform transform) {
        if(transform == null) {
            this.animationTransform = BoneAnimationTransform.ZERO;
            return;
        }
        this.animationTransform = transform;
    }

    @Override
    public BoneEntity<TViewer> getEntity() {
        return stand;
    }

    @Override
    public ModelBone<TViewer> getParent() {
        return parent;
    }

    @Override
    public void setParent(ModelBone<TViewer> parent) {
        this.parent = parent;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setGlobalScale(float scale) {
        this.scale = scale;
    }

    public Point calculateGlobalRotation(Point endPos) {
        return calculateRotation(endPos, new Vec(0, 180 - model.getGlobalRotation(), 0), this.model.getPivot());
    }

    public Point calculateRotation(Point p, Point rotation, Point pivot) {
        Point position = p.sub(pivot);
        return ModelMath.rotate(position, rotation).add(pivot);
    }

    @Override
    public Point calculateScale(Point p, Point scale, Point pivot) {
        Point position = p.sub(pivot);
        return position.mul(scale).add(pivot);
    }

    public Point applyTransform(Point p) {
        Point endPos = p;

        if (this.diff != null) {
            endPos = calculateScale(endPos, this.getPropogatedScale(), this.pivot.sub(this.diff));
            endPos = calculateRotation(endPos, this.getPropogatedRotation(), this.pivot.sub(this.diff));
        } else {
            endPos = calculateScale(endPos, this.getPropogatedScale(), this.pivot);
            endPos = calculateRotation(endPos, this.getPropogatedRotation(), this.pivot);
        }

        endPos = endPos.add(animationTransform.translation());

        if (this.parent != null) {
            endPos = parent.applyTransform(endPos);
        }

        return endPos;
    }

    public Point getPropogatedRotation() {
        Point netTransform = Vec.ZERO;
        netTransform = netTransform.add(this.animationTransform.rotation());
        return this.rotation.add(netTransform);
    }

    @Override
    public Point getPropogatedScale() {
        Point netTransform = Vec.ONE;

        netTransform = netTransform.mul(animationTransform.scale());

        return netTransform;
    }

    @Override
    public Point calculateFinalScale(Point q) {
        if (this.parent != null) {
            Point pq = parent.calculateFinalScale(parent.getPropogatedScale());
            q = pq.mul(q);
        }

        return q;
    }

    public Quaternion calculateFinalAngle(Quaternion q) {
        if (this.parent != null) {
            Quaternion pq = parent.calculateFinalAngle(new Quaternion(parent.getPropogatedRotation()));
            q = pq.multiply(q);
        }

        return q;
    }

    public void addChild(ModelBone<TViewer> child) {
        this.children.add(child);
    }

    @Override
    public void destroy() {
        this.children.forEach(ModelBone::destroy);
        this.children.clear();

        if (this.stand != null) {
            this.stand.remove();
        }
    }

    @Override
    public Point getOffset() {
        return this.offset;
    }

    public abstract Pos calculatePosition();

    public abstract Point calculateRotation();

    public abstract Point calculateScale();
}
