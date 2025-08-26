package net.worldseed.multipart;

import net.minestom.server.entity.Player;
import net.worldseed.multipart.model_bones.display_entity.RootBoneEntity;
import net.worldseed.multipart.model_bones.entity.AbstractBoneEntity;
import net.worldseed.multipart.model_bones.entity.AbstractItemDisplayBoneEntity;
import net.worldseed.multipart.model_bones.EntityFactory;
import net.worldseed.multipart.model_bones.ItemDisplayBoneEntity;

public class MinestomEntityFactory implements EntityFactory<Player> {

    public static MinestomEntityFactory INSTANCE = new MinestomEntityFactory();

    @Override
    public AbstractItemDisplayBoneEntity<Player> createItemDisplayBoneEntity(GenericModel<Player> model, String name) {
        return new ItemDisplayBoneEntity((MinestomModel) model, name);
    }

    @Override
    public AbstractBoneEntity<Player> createRootEntity(GenericModel<Player> model) {
        return new RootBoneEntity((MinestomModel) model);
    }
}
