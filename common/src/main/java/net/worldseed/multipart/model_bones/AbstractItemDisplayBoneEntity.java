package net.worldseed.multipart.model_bones;

import net.kyori.adventure.util.RGBLike;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Vec;

public interface AbstractItemDisplayBoneEntity<TViewer> extends AbstractBoneEntity<TViewer> {

    void setGlowing(RGBLike color);

    void setGlowing(TViewer player, RGBLike color);

    void clearItem();

    void setItemState(String state);

    void setTransformationInterpolationStartDelta(int i);

    void setScale(Vec vec);

    void setRightRotation(float[] floats);

    void setTranslation(Pos position);

    void setTransformationInterpolationDuration(int i);

    void setPosRotInterpolationDuration(int i);

    void setViewRange(int i);

    void setFixedContext();

}
