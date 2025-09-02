package net.worldseed.multipart.entity.entity;

import net.worldseed.multipart.math.Pos;

public interface BoneEntity<TViewer> {

    int getEntityId();

    void remove();

    boolean addViewer(TViewer player);

    boolean removeViewer(TViewer player);

    void setGlowing(boolean b);

    void setGlowing(TViewer viewer, boolean b);

    void setRotation(float yaw, float pitch);

    void teleport(Pos pos);

    Pos getLocation();

    boolean isRemoved();

    double getDistanceSquared(Pos newPos);

    void setNotifyAboutChanges(boolean b);

}
