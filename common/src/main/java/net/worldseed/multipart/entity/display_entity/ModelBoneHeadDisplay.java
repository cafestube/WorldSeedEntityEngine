package net.worldseed.multipart.entity.display_entity;

import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.animations.AnimationLoader;
import net.worldseed.multipart.blueprint.ModelRenderInformation;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.entity.bone_types.HeadBone;

import java.util.Map;

public class ModelBoneHeadDisplay<TViewer> extends ModelBonePartDisplay<TViewer> implements HeadBone<TViewer> {
    private double headRotation;

    public ModelBoneHeadDisplay(Point pivot, String name, Point rotation, Point diff, Point offset, GenericModel<TViewer> model, float scale, Map<String, ModelRenderInformation> renderInfo) {
        super(pivot, name, rotation, diff, offset, model, scale, renderInfo);
    }

    @Override
    public Point getPropogatedRotation() {
        Point netTransform = Vec.ZERO;
        netTransform = netTransform.add(this.getAnimationTransform().rotation());

        return this.rotation.add(netTransform).add(0, this.headRotation, 0);
    }

    public void setRotation(double rotation) {
        this.headRotation = rotation;
    }

    @Override
    public double getRotation() {
        return this.headRotation;
    }
}
