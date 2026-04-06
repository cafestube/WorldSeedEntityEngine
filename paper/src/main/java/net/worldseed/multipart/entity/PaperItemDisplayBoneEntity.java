package net.worldseed.multipart.entity;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.RGBLike;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.item.ItemDisplayContext;
import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.blueprint.ModelRenderInformation;
import net.worldseed.multipart.entity.util.DataWatcher;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Quaternion;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.entity.entity.ItemDisplayBoneEntity;
import net.worldseed.multipart.entity.util.EntityData;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;

public class PaperItemDisplayBoneEntity extends PaperPacketBoneEntity implements ItemDisplayBoneEntity<Player> {

    public PaperItemDisplayBoneEntity(PaperModel model, String name) {
        super(EntityType.ITEM_DISPLAY, model, name);
    }

    @Override
    public void setGlowing(RGBLike color) {
        int rgb = 0;
        rgb |= color.red() << 16;
        rgb |= color.green() << 8;
        rgb |= color.blue();

        boolean previous = this.dataWatcher.setNotifyAboutChanges(false);
        this.dataWatcher.set(EntityData.DISPLAY_DATA_GLOW_COLOR_OVERRIDE_ID, rgb);
        super.setGlowing(true);
        this.dataWatcher.setNotifyAboutChanges(previous);
    }

    @Override
    public void setGlowing(Player player, RGBLike color) {
        int rgb = 0;
        rgb |= color.red() << 16;
        rgb |= color.green() << 8;
        rgb |= color.blue();

        DataWatcher watcher = new DataWatcher(null);
        watcher.set(EntityData.DISPLAY_DATA_GLOW_COLOR_OVERRIDE_ID, rgb);
        watcher.setSharedFlag(6, true); // glowing
        sendPacket(player, new ClientboundSetEntityDataPacket(entityId, watcher.packDirty()));
    }

    @Override
    public void setItemState(@Nullable ModelRenderInformation itemState) {
        if(itemState == null) {
            this.dataWatcher.set(EntityData.ITEM_DISPLAY_DATA_ITEM_STACK_ID, net.minecraft.world.item.ItemStack.EMPTY);
            return;
        }
        ItemStack itemStack = ItemType.PAPER.createItemStack();
        itemStack.setData(DataComponentTypes.ITEM_MODEL, itemState.itemModel());
        itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                .addFloat(itemState.modelStateId())
                .build());


        this.dataWatcher.set(EntityData.ITEM_DISPLAY_DATA_ITEM_STACK_ID, CraftItemStack.asNMSCopy(
            itemStack
        ));
    }

    @Override
    public void setTransformationInterpolationStartDelta(int i) {
        this.dataWatcher.set(EntityData.DISPLAY_DATA_TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID, i);
    }

    @Override
    public void setScale(Vec vec) {
        this.dataWatcher.set(EntityData.DISPLAY_DATA_SCALE_ID, new Vector3f((float) vec.x(), (float) vec.y(), (float) vec.z()));
    }

    @Override
    public void setRightRotation(Quaternion quaternion) {
        this.dataWatcher.set(EntityData.DISPLAY_DATA_RIGHT_ROTATION_ID, new Quaternionf(quaternion.x(), quaternion.y(), quaternion.z(), quaternion.w()));
    }

    @Override
    public void setTranslation(Point position) {
        this.dataWatcher.set(EntityData.DISPLAY_DATA_TRANSLATION_ID, new Vector3f((float) position.x(), (float) position.y(), (float) position.z()));
    }

    @Override
    public void setTransformationInterpolationDuration(int i) {
        this.dataWatcher.set(EntityData.DISPLAY_DATA_TRANSFORMATION_INTERPOLATION_DURATION_ID, i);
    }

    @Override
    public void setPosRotInterpolationDuration(int i) {
        this.dataWatcher.set(EntityData.DISPLAY_DATA_POS_ROT_INTERPOLATION_DURATION_ID, i);
    }

    @Override
    public void setViewRange(int i) {
        this.dataWatcher.set(EntityData.DISPLAY_DATA_VIEW_RANGE_ID, (float) i);
    }

    @Override
    public void setFixedContext() {
        this.dataWatcher.set(EntityData.ITEM_DISPLAY_DATA_ITEM_DISPLAY_ID, ItemDisplayContext.FIXED.getId());
    }
}
