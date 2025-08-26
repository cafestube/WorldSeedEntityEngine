package net.worldseed.multipart.model_bones.entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.RGBLike;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Vec;

public interface TextDisplayBoneEntity<TViewer> extends BoneEntity<TViewer> {

    void setText(Component text);

    Component getText();

    void setTranslation(Pos position);
}
