package net.worldseed.multipart.entity.display_entity;

import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.animations.AnimationLoader;
import net.worldseed.multipart.animations.BoneAnimation;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.entity.bone_types.HeadBone;

public class ModelBoneHeadDisplay<TViewer> extends ModelBonePartDisplay<TViewer> implements HeadBone<TViewer> {
    private double headRotation;

    public ModelBoneHeadDisplay(Point pivot, String name, Point rotation, GenericModel<TViewer> model, float scale) {
        super(pivot, name, rotation, model, scale);
    }

    @Override
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

        return this.rotation.add(netTransform).add(0, this.headRotation, 0);
    }

    public void setRotation(double rotation) {
        this.headRotation = rotation;
    }
}
