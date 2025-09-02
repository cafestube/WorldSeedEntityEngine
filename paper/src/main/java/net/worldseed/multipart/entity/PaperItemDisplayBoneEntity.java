package net.worldseed.multipart.entity;

import net.kyori.adventure.util.RGBLike;
import net.minecraft.world.item.ItemDisplayContext;
import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Quaternion;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.entity.entity.ItemDisplayBoneEntity;
import net.worldseed.multipart.entity.util.EntityData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;

public class PaperItemDisplayBoneEntity extends PaperPacketBoneEntity implements ItemDisplayBoneEntity<Player> {

    protected final HashMap<String, ItemStack> items;

    public PaperItemDisplayBoneEntity(PaperModel model, String name) {
        super(EntityType.ITEM_DISPLAY, model, name);

        this.items = model.getModelRegistry().getItems(model.getId(), name);
    }

    @Override
    public void setGlowing(RGBLike color) {
        //TODO:
    }

    @Override
    public void setGlowing(Player player, RGBLike color) {
        //TODO:
    }

    @Override
    public void clearItem() {
        this.dataWatcher.set(EntityData.ITEM_DISPLAY_DATA_ITEM_STACK_ID, net.minecraft.world.item.ItemStack.EMPTY);
    }

    @Override
    public void setItemState(String state) {
        if (this.items.containsKey(state)) {
            this.dataWatcher.set(EntityData.ITEM_DISPLAY_DATA_ITEM_STACK_ID, CraftItemStack.asNMSCopy(this.items.get(state)));
        }
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
