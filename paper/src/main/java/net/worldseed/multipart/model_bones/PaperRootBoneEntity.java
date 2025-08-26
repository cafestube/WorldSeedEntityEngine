package net.worldseed.multipart.model_bones;

import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.model_bones.entity.BoneEntity;
import net.worldseed.multipart.model_bones.entity.RootBoneEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PaperRootBoneEntity extends PaperPacketBoneEntity implements RootBoneEntity<Player> {

    public PaperRootBoneEntity(PaperModel model) {
        super(EntityType.ITEM_DISPLAY, model, "root");
    }

    @Override
    public void attachEntity(BoneEntity<Player> entity) {

    }


}
