package net.worldseed.multipart;

import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.model_bones.PaperPacketBoneEntity;
import net.worldseed.multipart.model_bones.PaperItemDisplayBoneEntity;
import net.worldseed.multipart.model_bones.PaperTextDisplayBoneEntity;
import net.worldseed.multipart.model_bones.PaperRootBoneEntity;
import net.worldseed.multipart.model_bones.entity.BoneEntity;
import net.worldseed.multipart.model_bones.entity.ItemDisplayBoneEntity;
import net.worldseed.multipart.model_bones.EntityFactory;
import net.worldseed.multipart.model_bones.entity.RootBoneEntity;
import net.worldseed.multipart.model_bones.entity.TextDisplayBoneEntity;
import org.bukkit.entity.Player;

public class PaperEntityFactory implements EntityFactory<Player> {

    public static PaperEntityFactory INSTANCE = new PaperEntityFactory();

    @Override
    public ItemDisplayBoneEntity<Player> createItemDisplayBoneEntity(GenericModel<Player> model, String name) {
        return new PaperItemDisplayBoneEntity((PaperModel) model, name);
    }

    @Override
    public RootBoneEntity<Player> createRootEntity(GenericModel<Player> model) {
        return new PaperRootBoneEntity((PaperModel) model);
    }

    @Override
    public TextDisplayBoneEntity<Player> createTextDisplayBoneEntity(GenericModel<Player> model, String name) {
        return new PaperTextDisplayBoneEntity((PaperModel) model, name);
    }

    @Override
    public void spawn(GenericModel<Player> model, BoneEntity<Player> entity, Pos position) {

    }
}
