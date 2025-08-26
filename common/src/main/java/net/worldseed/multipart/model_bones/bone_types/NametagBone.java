package net.worldseed.multipart.model_bones.bone_types;

import net.kyori.adventure.text.Component;
import net.worldseed.multipart.AbstractGenericModel;
import net.worldseed.multipart.model_bones.AbstractModelBone;
import org.jetbrains.annotations.Nullable;

public interface NametagBone<TPlayer, TBone extends AbstractModelBone<TPlayer, TModel, TBone>, TModel extends AbstractGenericModel<TPlayer, TBone, TModel>> extends AbstractModelBone<TPlayer, TModel, TBone> {

    @Nullable Component getNametag();

    void setNametag(@Nullable Component component);

}