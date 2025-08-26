package net.worldseed.multipart;

import net.minestom.server.entity.Player;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.model_bones.MinestomBoneEntity;
import net.worldseed.multipart.model_bones.MinestomItemDisplayBoneEntity;
import net.worldseed.multipart.model_bones.MinestomTextDisplayBoneEntity;
import net.worldseed.multipart.model_bones.display_entity.MinestomRootBoneEntity;
import net.worldseed.multipart.model_bones.entity.BoneEntity;
import net.worldseed.multipart.model_bones.entity.ItemDisplayBoneEntity;
import net.worldseed.multipart.model_bones.EntityFactory;
import net.worldseed.multipart.model_bones.entity.RootBoneEntity;
import net.worldseed.multipart.model_bones.entity.TextDisplayBoneEntity;

public class MinestomEntityFactory implements EntityFactory<Player> {

    public static MinestomEntityFactory INSTANCE = new MinestomEntityFactory();

    @Override
    public ItemDisplayBoneEntity<Player> createItemDisplayBoneEntity(GenericModel<Player> model, String name) {
        return new MinestomItemDisplayBoneEntity((MinestomModel) model, name);
    }

    @Override
    public RootBoneEntity<Player> createRootEntity(GenericModel<Player> model) {
        return new MinestomRootBoneEntity((MinestomModel) model);
    }

    @Override
    public TextDisplayBoneEntity<Player> createTextDisplayBoneEntity(GenericModel<Player> model, String name) {
        return new MinestomTextDisplayBoneEntity((MinestomModel) model, name);
    }

    @Override
    public void spawn(GenericModel<Player> model, BoneEntity<Player> entity, Pos position) {
        if(entity instanceof MinestomBoneEntity boneEntity && model instanceof MinestomModel minestomModel) {
            boneEntity.setInstance(minestomModel.getInstance(), position).join();
        }
    }
}
