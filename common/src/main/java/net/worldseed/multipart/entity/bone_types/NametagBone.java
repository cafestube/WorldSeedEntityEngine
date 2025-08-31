package net.worldseed.multipart.entity.bone_types;

import net.kyori.adventure.text.Component;
import net.worldseed.multipart.entity.ModelBone;
import org.jetbrains.annotations.Nullable;

public interface NametagBone<TPlayer> extends ModelBone<TPlayer> {

    @Nullable Component getNametag();

    void setNametag(@Nullable Component component);

}