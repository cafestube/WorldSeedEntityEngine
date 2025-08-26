package net.worldseed.multipart.model_bones.bone_types;

import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.model_bones.ModelBone;

public interface HeadBone<TPlayer> extends ModelBone<TPlayer> {
    /**
     * Set the rotation of the head
     *
     * @param rotation the new rotation
     */
    void setRotation(double rotation);
}
