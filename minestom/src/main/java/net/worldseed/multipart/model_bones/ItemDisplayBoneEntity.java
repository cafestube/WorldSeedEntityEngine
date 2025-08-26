package net.worldseed.multipart.model_bones;

import net.kyori.adventure.util.RGBLike;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.model_bones.entity.AbstractItemDisplayBoneEntity;

import java.util.HashMap;
import java.util.Map;

public class ItemDisplayBoneEntity extends BoneEntity implements AbstractItemDisplayBoneEntity<Player> {

    protected final HashMap<String, ItemStack> items;

    public ItemDisplayBoneEntity(GenericModel model, String name) {
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
        meta.setItemStack(ItemStack.AIR);
    }

    @Override
    public void setItemState(String state) {
        if (this.items.containsKey(state)) {
            var meta = (ItemDisplayMeta) this.getEntityMeta();
            meta.setItemStack(this.items.get(state));
        }
    }

    @Override
    public void setTransformationInterpolationStartDelta(int i) {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        meta.setTransformationInterpolationStartDelta(i);
    }

    @Override
    public void setScale(Vec vec) {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        meta.setScale(PositionConversion.asMinestom(vec));
    }

    @Override
    public void setRightRotation(float[] floats) {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        meta.setRightRotation(floats);
    }

    @Override
    public void setTranslation(Pos position) {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        meta.setTranslation(PositionConversion.asMinestom(position));
    }

    @Override
    public void setTransformationInterpolationDuration(int i) {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        meta.setTransformationInterpolationDuration(i);
    }

    @Override
    public void setPosRotInterpolationDuration(int i) {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        meta.setPosRotInterpolationDuration(i);
    }

    @Override
    public void setViewRange(int i) {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        meta.setViewRange(i);
    }

    @Override
    public void setFixedContext() {
        var meta = (ItemDisplayMeta) this.getEntityMeta();
        meta.setDisplayContext(ItemDisplayMeta.DisplayContext.FIXED);
    }
}
