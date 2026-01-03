package net.worldseed.multipart.entity.bone_types;

import net.worldseed.multipart.entity.ModelBone;

public interface HeadBone<TPlayer> extends ModelBone<TPlayer> {
    /**
     * Set the rotation of the head
     *
     * @param rotation the new rotation
     */
    void setRotation(double rotation);

    double getRotation();
}
