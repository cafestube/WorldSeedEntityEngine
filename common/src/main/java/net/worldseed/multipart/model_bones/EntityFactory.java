package net.worldseed.multipart.model_bones;

import net.worldseed.multipart.AbstractGenericModel;

public interface EntityFactory<TViewer> {

    AbstractItemDisplayBoneEntity<TViewer> createItemDisplayBoneEntity(AbstractGenericModel<TViewer, ?, ?> model, String name);

}
