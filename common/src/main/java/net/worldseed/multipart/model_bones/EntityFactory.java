package net.worldseed.multipart.model_bones;

import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.model_bones.entity.BoneEntity;
import net.worldseed.multipart.model_bones.entity.ItemDisplayBoneEntity;
import net.worldseed.multipart.model_bones.entity.RootBoneEntity;
import net.worldseed.multipart.model_bones.entity.TextDisplayBoneEntity;

public interface EntityFactory<TViewer> {

    ItemDisplayBoneEntity<TViewer> createItemDisplayBoneEntity(GenericModel<TViewer> model, String name);

    RootBoneEntity<TViewer> createRootEntity(GenericModel<TViewer> model);

    TextDisplayBoneEntity<TViewer> createTextDisplayBoneEntity(GenericModel<TViewer> model, String name);

    void spawn(GenericModel<TViewer> model, BoneEntity<TViewer> entity, Pos position);

}
