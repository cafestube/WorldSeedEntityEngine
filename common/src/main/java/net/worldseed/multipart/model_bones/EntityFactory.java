package net.worldseed.multipart.model_bones;

import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.model_bones.entity.AbstractBoneEntity;
import net.worldseed.multipart.model_bones.entity.AbstractItemDisplayBoneEntity;

public interface EntityFactory<TViewer> {

    AbstractItemDisplayBoneEntity<TViewer> createItemDisplayBoneEntity(GenericModel<TViewer> model, String name);

    AbstractBoneEntity<TViewer> createRootEntity(GenericModel<TViewer> model);

}
