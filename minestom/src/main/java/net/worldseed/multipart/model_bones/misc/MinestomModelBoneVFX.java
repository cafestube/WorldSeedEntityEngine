package net.worldseed.multipart.model_bones.misc;

import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.model_bones.BoneEntity;
import net.worldseed.multipart.model_bones.ModelBone;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MinestomModelBoneVFX extends ModelBoneVFX<Player, GenericModel, ModelBone> implements ModelBone {

    public MinestomModelBoneVFX(Point pivot, String name, Point rotation, GenericModel model, float scale) {
        super(pivot, name, rotation, model, scale);
    }

    @Override
    public BoneEntity getEntity() {
        return (BoneEntity) super.getEntity();
    }

    @Override
    public CompletableFuture<Void> spawn(@Nullable Instance instance, Pos pos) {
        return CompletableFuture.completedFuture(null);
    }
}
