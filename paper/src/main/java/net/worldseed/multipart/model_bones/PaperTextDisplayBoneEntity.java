package net.worldseed.multipart.model_bones;

import net.kyori.adventure.text.Component;
import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.model_bones.entity.TextDisplayBoneEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PaperTextDisplayBoneEntity extends PaperPacketBoneEntity implements TextDisplayBoneEntity<Player> {

    public PaperTextDisplayBoneEntity(PaperModel model, String name) {
        super(EntityType.TEXT_DISPLAY, model, name);

    }

    @Override
    public void setText(Component text) {

    }

    @Override
    public Component getText() {
        return null;
    }

    @Override
    public void setTranslation(Pos position) {

    }
}
