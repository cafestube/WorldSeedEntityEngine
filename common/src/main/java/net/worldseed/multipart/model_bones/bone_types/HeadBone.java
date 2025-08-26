package net.worldseed.multipart.model_bones.bone_types;

import net.worldseed.multipart.AbstractGenericModel;
import net.worldseed.multipart.model_bones.ModelBone;

public interface HeadBone<TPlayer, TModel extends AbstractGenericModel<TPlayer,TModel>> extends ModelBone<TPlayer, TModel> {
    /**
     * Set the rotation of the head
     *
     * @param rotation the new rotation
     */
    void setRotation(double rotation);
}
