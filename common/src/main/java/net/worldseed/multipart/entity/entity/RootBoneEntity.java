package net.worldseed.multipart.entity.entity;

public interface RootBoneEntity<TViewer> extends BoneEntity<TViewer> {

    void attachEntity(BoneEntity<TViewer> entity);

}
