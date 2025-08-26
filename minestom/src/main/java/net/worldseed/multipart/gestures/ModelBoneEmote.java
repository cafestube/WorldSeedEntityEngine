package net.worldseed.multipart.gestures;

import net.kyori.adventure.util.RGBLike;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.CustomModelData;
import net.minestom.server.item.component.HeadProfile;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Quaternion;
import net.worldseed.multipart.model_bones.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ModelBoneEmote extends ModelBoneImpl<Player> implements ModelBoneViewable {
    private final Double verticalOffset;

    public ModelBoneEmote(Point pivot, String name, Point rotation, MinestomModel model, int translation, Double verticalOffset, PlayerSkin skin) {
        super(pivot, name, rotation, model, 1);

        this.verticalOffset = verticalOffset;

        if (this.offset != null) {
            MinestomBoneEntity entity = new MinestomBoneEntity(EntityType.ITEM_DISPLAY, model, name);
            this.stand = entity;
            entity.editEntityMeta(ItemDisplayMeta.class, meta -> {
                meta.setViewRange(10000);
                meta.setTransformationInterpolationDuration(2);
                meta.setPosRotInterpolationDuration(2);
                meta.setTranslation(new net.minestom.server.coordinate.Vec(0, translation, 0));
                meta.setDisplayContext(ItemDisplayMeta.DisplayContext.THIRDPERSON_RIGHT_HAND);

                meta.setItemStack(ItemStack.builder(Material.PLAYER_HEAD)
                        .set(DataComponents.PROFILE, new HeadProfile(skin))
                        .set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(customModelDataFromName(name)), List.of(), List.of(), List.of()))
                        .build()
                );
            });
        }

        switch (this.name) {
            case "Head" -> {
                this.diff = this.pivot.add(0, 0, 0);
            }
            case "RightArm" -> {
                this.diff = this.pivot.add(-1.17, 0, 0);
            }
            case "LeftArm" -> {
                this.diff = this.pivot.add(1.17, 0, 0);
            }
            case "RightLeg" -> {
                this.diff = this.pivot.add(-0.4446, 0, 0);
            }
            case "LeftLeg" -> {
                this.diff = this.pivot.add(0.4446, 0, 0);
            }
            case "Body" -> {
                this.diff = this.pivot.add(0, 0, 0);
            }
        }
    }

    @Override
    public MinestomBoneEntity getEntity() {
        return (MinestomBoneEntity) super.getEntity();
    }

    @Override
    public void draw() {
        this.children.forEach(ModelBone::draw);
        if (this.offset == null) return;

        MinestomBoneEntity entity = this.getEntity();
        
        if (entity != null) {
            var scale = calculateScale();
            var position = calculatePosition();

            if (entity.getEntityMeta() instanceof ItemDisplayMeta meta) {
                Quaternion q = new Quaternion(calculateRotation());

                meta.setNotifyAboutChanges(false);
                meta.setTransformationInterpolationStartDelta(0);
                meta.setScale(new net.minestom.server.coordinate.Vec(scale.x() * this.scale, scale.y() * this.scale, scale.z() * this.scale));
                meta.setRightRotation(new float[]{(float) q.x(), (float) q.y(), (float) q.z(), (float) q.w()});
                meta.setNotifyAboutChanges(true);

                entity.teleport(PositionConversion.asMinestom(position.withView((float) 0, 0)));
            }
        }
    }

    @Override
    public Pos calculatePosition() {
        Point p = this.offset == null ? Pos.ZERO : this.offset;
        p = applyTransform(p);
        p = calculateGlobalRotation(p);

        return Pos.fromPoint(p)
                .div(4, 4, 4).mul(scale)
                .add(model.getPosition())
                .add(0, verticalOffset, 0)
                .add(model.getGlobalOffset());
    }

    @Override
    public Point calculateRotation() {
        Quaternion q = calculateFinalAngle(new Quaternion(getPropogatedRotation()));
        Quaternion pq = new Quaternion(new Vec(0, 180 - this.model.getGlobalRotation(), 0));
        q = pq.multiply(q);

        return q.toEuler();
    }

    @Override
    public Point calculateScale() {
        return Vec.ONE;
    }

    private float customModelDataFromName(String name) {
        return switch (name) {
            case "Head" -> 1;
            case "RightArm" -> 2;
            case "LeftArm" -> 3;
            case "Body" -> 4;
            case "RightLeg" -> 5;
            case "LeftLeg" -> 6;
            case "slim_right" -> 7;
            case "slim_left" -> 8;
            default -> 0;
        };
    }

    @Override
    public void setState(String state) {
        throw new UnsupportedOperationException("Cannot set state on an emote");
    }

    @Override
    public Point getPosition() {
        return calculatePosition();
    }

    @Override
    public void addViewer(Player player) {
        MinestomBoneEntity entity = this.getEntity();
        if (entity != null) entity.addViewer(player);
    }

    @Override
    public void removeViewer(Player player) {
        MinestomBoneEntity entity = this.getEntity();
        if (entity != null) entity.removeViewer(player);
    }

    @Override
    public void removeGlowing() {
        MinestomBoneEntity entity = this.getEntity();
        if (entity != null) entity.setGlowing(false);
    }

    @Override
    public void setGlowing(RGBLike color) {
        MinestomBoneEntity entity = this.getEntity();
        if (entity != null) entity.setGlowing(true);
    }

    @Override
    public void removeGlowing(Player player) {

    }

    @Override
    public void setGlowing(Player player, RGBLike color) {

    }

    @Override
    public void attachModel(GenericModel<Player> model) {
        throw new UnsupportedOperationException("Cannot attach a model to this bone type");
    }

    @Override
    public List<GenericModel<Player>> getAttachedModels() {
        return List.of();
    }

    @Override
    public void detachModel(GenericModel<Player> model) {
        throw new UnsupportedOperationException("Cannot detach a model from this bone type");
    }

    @Override
    public @NotNull Collection<ModelBone<Player>> getChildren() {
        return List.of();
    }

    @Override
    public void setGlobalRotation(double yaw, double pitch) {
    }
}