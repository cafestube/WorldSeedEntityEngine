package net.worldseed.multipart.entity;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.Display;
import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.entity.util.EntityData;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.entity.entity.TextDisplayBoneEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.joml.Vector3f;

public class PaperTextDisplayBoneEntity extends PaperPacketBoneEntity implements TextDisplayBoneEntity<Player> {

    public PaperTextDisplayBoneEntity(PaperModel model, String name) {
        super(EntityType.TEXT_DISPLAY, model, name);
        this.dataWatcher.set(EntityData.DISPLAY_DATA_BILLBOARD_RENDER_CONSTRAINTS_ID, (byte) Display.BillboardConstraints.VERTICAL.ordinal());
    }

    @Override
    public Component getText() {
        return PaperAdventure.asAdventure(this.dataWatcher.get(EntityData.TEXT_DISPLAY_DATA_TEXT_ID));
    }

    @Override
    public void setText(Component text) {
        this.dataWatcher.set(EntityData.TEXT_DISPLAY_DATA_TEXT_ID, PaperAdventure.asVanilla(text));
    }

    @Override
    public void setTranslation(Pos position) {
        this.dataWatcher.set(EntityData.DISPLAY_DATA_TRANSLATION_ID, new Vector3f((float) position.x(), (float) position.y(), (float) position.z()));
    }
}
