package net.worldseed.multipart.model_bones.bone_types;

import net.worldseed.multipart.AbstractGenericModel;
import net.worldseed.multipart.model_bones.AbstractModelBone;

public interface HeadBone<TPlayer, TBone extends AbstractModelBone<TPlayer, TModel, TBone>, TModel extends AbstractGenericModel<TPlayer, TBone, TModel>> extends AbstractModelBone<TPlayer, TModel, TBone> {
    /**
     * Set the rotation of the head
     *
     * @param rotation the new rotation
     */
    void setRotation(double rotation);
}
