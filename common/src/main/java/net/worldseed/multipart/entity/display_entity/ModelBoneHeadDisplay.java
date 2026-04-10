package net.worldseed.multipart.entity.display_entity;

import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.blueprint.ModelRenderInformation;
import net.worldseed.multipart.math.ModelMath;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.entity.bone_types.HeadBone;

import java.util.Map;

public class ModelBoneHeadDisplay<TViewer> extends ModelBonePartDisplay<TViewer> implements HeadBone<TViewer> {
    private boolean hasCustomHeadRotation = false;
    private double headRotationY;
    private double headRotationX;

    public ModelBoneHeadDisplay(Point pivot, String name, Point rotation, Point diff, Point offset, GenericModel<TViewer> model, float scale, Map<String, ModelRenderInformation> renderInfo) {
        super(pivot, name, rotation, diff, offset, model, scale, renderInfo);
    }

    @Override
    public Point getPropogatedRotation() {
        //Don't mess with head rotation if no rotation was applied.
        if(!hasCustomHeadRotation) return super.getPropogatedRotation();
        double rotateHead = ModelMath.differenceDegrees(this.headRotationY, model.getGlobalYRotation());

        return this.rotation.add(this.getAnimationTransform().rotation())
                .add(-this.headRotationX, rotateHead, 0);
    }

    @Override
    public void setXRotation(double pitch) {
        this.headRotationX = pitch;
        this.hasCustomHeadRotation = true;
    }

    @Override
    public double getXRotation() {
        return this.headRotationX;
    }

    public void setYRotation(double rotation) {
        this.headRotationY = rotation;
        this.hasCustomHeadRotation = true;
    }

    @Override
    public double getYRotation() {
        return this.headRotationY;
    }
}
