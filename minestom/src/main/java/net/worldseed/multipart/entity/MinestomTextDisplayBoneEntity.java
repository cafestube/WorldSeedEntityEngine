package net.worldseed.multipart.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.entity.entity.TextDisplayBoneEntity;

public class MinestomTextDisplayBoneEntity extends MinestomBoneEntity implements TextDisplayBoneEntity<Player> {

    public MinestomTextDisplayBoneEntity(MinestomModel model, String name) {
        super(EntityType.TEXT_DISPLAY, model, name);

        var meta = (TextDisplayMeta) this.getEntityMeta();
        meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.VERTICAL);
    }

    @Override
    public void setText(Component text) {
        var meta = (TextDisplayMeta) this.getEntityMeta();
        meta.setText(text);
    }

    @Override
    public Component getText() {
        var meta = (TextDisplayMeta) this.getEntityMeta();
        return meta.getText();
    }

    @Override
    public void setTranslation(Pos position) {
        var meta = (TextDisplayMeta) this.getEntityMeta();
        meta.setTranslation(PositionConversion.asMinestom(position));
    }
}
