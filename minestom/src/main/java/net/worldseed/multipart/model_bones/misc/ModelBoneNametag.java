package net.worldseed.multipart.model_bones.misc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.model_bones.BoneEntity;
import net.worldseed.multipart.model_bones.ModelBone;
import net.worldseed.multipart.model_bones.ModelBoneImpl;
import net.worldseed.multipart.model_bones.bone_types.NametagBone;

import java.util.List;

public class ModelBoneNametag extends ModelBoneImpl implements NametagBone<Player, ModelBone, GenericModel> {
    public ModelBoneNametag(Point pivot, String name, Point rotation, GenericModel model, float scale) {
        super(pivot, name, rotation, model, scale);
    }

    @Override
    public void addViewer(Player player) {
        if (this.stand != null) this.stand.addViewer(player);
    }

    @Override
    public void removeViewer(Player player) {
        if (this.stand != null) this.stand.removeViewer(player);
    }

    @Override
    public void removeGlowing() {
    }

    @Override
    public void setGlowing(RGBLike color) {
    }

    @Override
    public void removeGlowing(Player player) {
    }

    @Override
    public void setGlowing(Player player, RGBLike color) {
    }

    @Override
    public void attachModel(GenericModel model) {
        throw new UnsupportedOperationException("Cannot attach a model to a nametag");
    }

    @Override
    public List<GenericModel> getAttachedModels() {
        return List.of();
    }

    @Override
    public void detachModel(GenericModel model) {
        throw new UnsupportedOperationException("Cannot detach a model from a nametag");
    }

    @Override
    public void setGlobalRotation(double yaw, double pitch) {
    }

    @Override
    public void setState(String state) {
    }

    @Override
    public Point getPosition() {
        return calculatePosition();
    }

    public void draw() {
        if (this.offset == null || stand == null) return;

        this.stand.editEntityMeta(TextDisplayMeta.class, textDisplayMeta -> {
            textDisplayMeta.setTranslation(PositionConversion.asMinestom(calculatePositionInternal()));
        });
    }

    @Override
    public Pos calculatePosition() {
        if (this.stand == null) return Pos.ZERO;
        if (this.offset == null) return Pos.ZERO;

        Point p = this.offset;
        p = applyTransform(p);
        p = calculateGlobalRotation(p);

        return Pos.fromPoint(p)
                .div(4, 4, 4).mul(scale)
                .add(model.getPosition())
                .add(model.getGlobalOffset());
    }

    @Override
    public Point calculateRotation() {
        return Vec.ZERO;
    }

    @Override
    public Point calculateScale() {
        return Vec.ZERO;
    }

    private Pos calculatePositionInternal() {
        if (this.offset == null) return Pos.ZERO;
        Point p = this.offset;
        p = applyTransform(p);
        return new Pos(p).div(4).mul(scale).withView(0, 0);
    }

    @Override
    public void setNametag(Component component) {
        if(component == null && this.stand == null) return;

        if(component == null) {
            this.stand.remove();
            this.stand = null;
            return;
        }

        if(this.stand != null && (this.stand.isActive() || this.model.getInstance() == null)) {
            this.stand.editEntityMeta(TextDisplayMeta.class, textDisplayMeta -> {
                textDisplayMeta.setText(component);
            });
            return;
        }

        this.stand = new BoneEntity(EntityType.TEXT_DISPLAY, this.model, this.name);
        this.stand.editEntityMeta(TextDisplayMeta.class, textDisplayMeta -> {
            textDisplayMeta.setText(component);
            textDisplayMeta.setTranslation(PositionConversion.asMinestom(calculatePositionInternal()));
            textDisplayMeta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.VERTICAL);
        });

        if(this.model.getInstance() != null) {
            this.stand.setInstance(model.getInstance(), model.getPosition());
            this.model.getModelRoot().addPassenger(this.stand);
        }
    }

    @Override
    public Component getNametag() {
        if(this.stand == null) return null;
        return ((TextDisplayMeta) this.stand.getEntityMeta()).getText();
    }
}
