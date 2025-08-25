package net.worldseed.multipart.model_bones.bone_types;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.worldseed.multipart.model_bones.ModelBone;
import org.jetbrains.annotations.Nullable;

public interface NametagBone extends ModelBone {

    @Nullable Component getNametag();

    void setNametag(@Nullable Component component);

}