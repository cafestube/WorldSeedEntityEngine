package net.worldseed.multipart.model_bones;

import net.worldseed.multipart.AbstractGenericModel;
import net.worldseed.multipart.animations.AnimationLoader;
import net.worldseed.multipart.animations.BoneAnimation;
import net.worldseed.multipart.math.*;
import net.worldseed.multipart.model_bones.entity.AbstractBoneEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractModelBoneImpl<TViewer, TModel extends AbstractGenericModel<TViewer, ?, ?>, TBone extends AbstractModelBone<TViewer, TModel, TBone>> implements AbstractModelBone<TViewer, TModel, TBone> {
    protected final Point pivot;
    protected final String name;
    protected final List<BoneAnimation> allAnimations = new ArrayList<>();
    protected final ArrayList<TBone> children = new ArrayList<>();
    protected final TModel model;
    protected Point diff;
    protected float scale;
    protected Point offset;
    protected Point rotation;
    protected AbstractBoneEntity<TViewer> stand;
    private TBone parent;

    public AbstractModelBoneImpl(Point pivot, String name, Point rotation, TModel model, float scale) {
        this.name = name;
        this.rotation = rotation;
        this.model = model;

        this.diff = model.getDiff(name);
        this.offset = model.getOffset(name);

        if (this.diff != null) this.pivot = pivot.add(this.diff);
        else this.pivot = pivot;

        this.scale = scale;
    }

    @Override
    public AbstractBoneEntity<TViewer> getEntity() {
        return stand;
    }

    @Override
    public TBone getParent() {
        return parent;
    }

    @Override
    public void setParent(TBone parent) {
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

        for (BoneAnimation currentAnimation : this.allAnimations) {
            if (currentAnimation != null && currentAnimation.isPlaying()) {
                if (currentAnimation.getType() == AnimationLoader.AnimationType.TRANSLATION) {
                    var calculatedTransform = currentAnimation.getTransform();
                    endPos = endPos.add(calculatedTransform);
                }
            }
        }

        if (this.parent != null) {
            endPos = parent.applyTransform(endPos);
        }

        return endPos;
    }

    public Point getPropogatedRotation() {
        Point netTransform = Vec.ZERO;

        for (BoneAnimation currentAnimation : this.allAnimations) {
            if (currentAnimation != null && currentAnimation.isPlaying()) {
                if (currentAnimation.getType() == AnimationLoader.AnimationType.ROTATION) {
                    Point calculatedTransform = currentAnimation.getTransform();
                    netTransform = netTransform.add(calculatedTransform);
                }
            }
        }

        return this.rotation.add(netTransform);
    }

    @Override
    public Point getPropogatedScale() {
        Point netTransform = Vec.ONE;

        for (BoneAnimation currentAnimation : this.allAnimations) {
            if (currentAnimation != null && currentAnimation.isPlaying()) {
                if (currentAnimation.getType() == AnimationLoader.AnimationType.SCALE) {
                    Point calculatedTransform = currentAnimation.getTransform();
                    netTransform = netTransform.mul(calculatedTransform);
                }
            }
        }

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

    public void addAnimation(BoneAnimation animation) {
        this.allAnimations.add(animation);
    }

    public void addChild(TBone child) {
        this.children.add(child);
    }

    @Override
    public void destroy() {
        this.children.forEach(TBone::destroy);
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
