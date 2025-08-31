package net.worldseed.multipart.entity.entity;

import net.kyori.adventure.text.Component;
import net.worldseed.multipart.math.Pos;

public interface TextDisplayBoneEntity<TViewer> extends BoneEntity<TViewer> {

    void setText(Component text);

    Component getText();

    void setTranslation(Pos position);
}
