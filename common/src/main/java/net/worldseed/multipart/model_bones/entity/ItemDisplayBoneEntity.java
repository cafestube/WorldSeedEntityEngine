package net.worldseed.multipart.model_bones.entity;

import net.kyori.adventure.util.RGBLike;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Quaternion;
import net.worldseed.multipart.math.Vec;

public interface ItemDisplayBoneEntity<TViewer> extends BoneEntity<TViewer> {

    void setGlowing(RGBLike color);

    void setGlowing(TViewer player, RGBLike color);

    void clearItem();

    void setItemState(String state);

    void setTransformationInterpolationStartDelta(int i);

    void setScale(Vec vec);

    void setRightRotation(Quaternion quaternion);

    void setTranslation(Point position);

    void setTransformationInterpolationDuration(int i);

    void setPosRotInterpolationDuration(int i);

    void setViewRange(int i);

    void setFixedContext();

}
