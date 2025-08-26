package net.worldseed.multipart.model_bones;

import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.math.Pos;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface ModelBone extends AbstractModelBone<Player, GenericModel, ModelBone> {

    CompletableFuture<Void> spawn(@Nullable Instance instance, Pos pos);

    @Override
    BoneEntity getEntity();
}
