package net.worldseed.multipart.model_bones.bone_types;

import net.kyori.adventure.text.Component;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.model_bones.ModelBone;
import org.jetbrains.annotations.Nullable;

public interface NametagBone<TPlayer> extends ModelBone<TPlayer> {

    @Nullable Component getNametag();

    void setNametag(@Nullable Component component);

}