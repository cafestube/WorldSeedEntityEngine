package net.worldseed.multipart.model_bones.display_entity;

import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.model_bones.*;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MinestomModelBonePartDisplay extends ModelBonePartDisplay<Player, GenericModel, ModelBone> implements ModelBone {

    public MinestomModelBonePartDisplay(Point pivot, String name, Point rotation, GenericModel model, float scale) {
        super(pivot, name, rotation, model, scale);
    }

    @Override
    public CompletableFuture<Void> spawn(@Nullable Instance instance, Pos pos) {
        var correctLocation = new Pos(pos).withYaw((float) (180 + this.model.getGlobalRotation() + 360) % 360);

        BoneEntity entity = this.getEntity();
        if (this.offset != null && entity != null) {
            entity.setNoGravity(true);
            entity.setSilent(true);
            return entity.setInstance(instance, correctLocation);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public BoneEntity getEntity() {
        return (BoneEntity) super.getEntity();
    }
}
