package net.worldseed.multipart.entity;

import net.kyori.adventure.util.RGBLike;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Quaternion;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.entity.entity.ItemDisplayBoneEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MinestomItemDisplayBoneEntity extends MinestomBoneEntity implements ItemDisplayBoneEntity<Player> {

    protected final HashMap<String, ItemStack> items;

    public MinestomItemDisplayBoneEntity(MinestomModel model, String name) {
        super(EntityType.ITEM_DISPLAY, model, name);

        this.items = model.getModelRegistry().getItems(model.getId(), name);
    }

    @Override
    public void setGlowing(RGBLike color) {
        int rgb = 0;
        rgb |= color.red() << 16;
        rgb |= color.green() << 8;
        rgb |= color.blue();

        var meta = (ItemDisplayMeta) this.getEntityMeta();
        if(meta.isHasGlowingEffect() && Objects.equals(meta.getGlowColorOverride(), rgb)) return;

        meta.setHasGlowingEffect(true);
        meta.setGlowColorOverride(rgb);
    }

    @Override
    public void setGlowing(Player player, RGBLike color) {
        int rgb = 0;
        rgb |= color.red() << 16;
        rgb |= color.green() << 8;
        rgb |= color.blue();

        EntityMetaDataPacket oldMetadataPacket = this.getMetadataPacket();
        Map<Integer, Metadata.Entry<?>> oldEntries = oldMetadataPacket.entries();
        byte previousFlags = oldEntries.containsKey(0)
                ? (byte) oldEntries.get(0).value()
                : 0;

        Map<Integer, Metadata.Entry<?>> entries = new HashMap<>(oldEntries);
        entries.put(0, Metadata.Byte((byte) (previousFlags | 0x40)));
        entries.put(22, Metadata.VarInt(rgb));

        player.sendPacket(new EntityMetaDataPacket(this.getEntityId(), entries));
    }

    @Override
    public void clearItem() {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        if(meta.getItemStack().isAir()) return;
        meta.setItemStack(ItemStack.AIR);
    }

    @Override
    public void setItemState(String state) {
        if (this.items.containsKey(state)) {
            var meta = (ItemDisplayMeta) this.getEntityMeta();
            if(Objects.equals(meta.getItemStack(), this.items.get(state))) return;
            meta.setItemStack(this.items.get(state));
        }
    }

    @Override
    public void setTransformationInterpolationStartDelta(int i) {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        if(meta.getTransformationInterpolationStartDelta() == i) return;
        meta.setTransformationInterpolationStartDelta(i);
    }

    @Override
    public void setScale(Vec vec) {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        var vector = PositionConversion.asMinestom(vec);
        if(Objects.equals(meta.getScale(), vector)) return;
        meta.setScale(vector);
    }

    @Override
    public void setRightRotation(Quaternion quaternion) {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        float[] floats = {(float) quaternion.x(), (float) quaternion.y(), (float) quaternion.z(), (float) quaternion.w()};
        if(Arrays.equals(meta.getRightRotation(), floats)) return;
        meta.setRightRotation(floats);
    }

    @Override
    public void setTranslation(Point position) {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        var pos = PositionConversion.asMinestom(position);
        if(Objects.equals(meta.getTranslation(), pos)) return;
        meta.setTranslation(pos);
    }

    @Override
    public void setTransformationInterpolationDuration(int i) {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        if(meta.getTransformationInterpolationDuration() == i) return;
        meta.setTransformationInterpolationDuration(i);
    }

    @Override
    public void setPosRotInterpolationDuration(int i) {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        if(meta.getPosRotInterpolationDuration() == i) return;
        meta.setPosRotInterpolationDuration(i);
    }

    @Override
    public void setViewRange(int i) {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        if(meta.getViewRange() == i) return;
        meta.setViewRange(i);
    }

    @Override
    public void setFixedContext() {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        if(meta.getDisplayContext() == ItemDisplayMeta.DisplayContext.FIXED) return;
        meta.setDisplayContext(ItemDisplayMeta.DisplayContext.FIXED);
    }
}
