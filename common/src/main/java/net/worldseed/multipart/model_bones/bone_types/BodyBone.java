package net.worldseed.multipart.model_bones.bone_types;

import net.worldseed.multipart.AbstractGenericModel;
import net.worldseed.multipart.model_bones.AbstractModelBone;

public interface BodyBone<TPlayer, TBone extends AbstractModelBone<TPlayer, TModel, TBone>, TModel extends AbstractGenericModel<TPlayer, TBone, TModel>> extends AbstractModelBone<TPlayer, TModel, TBone> {
}
