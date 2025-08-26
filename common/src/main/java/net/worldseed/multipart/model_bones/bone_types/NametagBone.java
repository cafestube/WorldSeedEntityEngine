package net.worldseed.multipart.model_bones.bone_types;

import net.kyori.adventure.text.Component;
import net.worldseed.multipart.AbstractGenericModel;
import net.worldseed.multipart.model_bones.ModelBone;
import org.jetbrains.annotations.Nullable;

public interface NametagBone<TPlayer, TModel extends AbstractGenericModel<TPlayer, TModel>> extends ModelBone<TPlayer, TModel> {

    @Nullable Component getNametag();

    void setNametag(@Nullable Component component);

}