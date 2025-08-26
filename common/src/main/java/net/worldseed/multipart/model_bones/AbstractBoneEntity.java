package net.worldseed.multipart.model_bones;

import net.kyori.adventure.util.RGBLike;

public interface AbstractBoneEntity<TViewer> {

    void remove();

    boolean addViewer(TViewer player);

    boolean removeViewer(TViewer player);

    void setGlowing(boolean b);

    void setGlowing(TViewer viewer, boolean b);

    void setRotation(float yaw, float pitch);

}
