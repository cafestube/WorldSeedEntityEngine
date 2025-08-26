package net.worldseed.multipart.model_bones.entity;

public interface RootBoneEntity<TViewer> extends BoneEntity<TViewer> {

    void attachEntity(BoneEntity<TViewer> entity);

}
