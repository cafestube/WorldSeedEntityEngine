package net.worldseed.multipart.model_bones;

import net.kyori.adventure.util.RGBLike;
import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.model_bones.entity.ItemDisplayBoneEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PaperItemDisplayBoneEntity extends PaperPacketBoneEntity implements ItemDisplayBoneEntity<Player> {

    protected final HashMap<String, ItemStack> items;

    public PaperItemDisplayBoneEntity(PaperModel model, String name) {
        super(EntityType.ITEM_DISPLAY, model, name);

        this.items = model.getModelRegistry().getItems(model.getId(), name);
    }

    @Override
    public void setGlowing(RGBLike color) {

    }

    @Override
    public void setGlowing(Player player, RGBLike color) {

    }

    @Override
    public void clearItem() {

    }

    @Override
    public void setItemState(String state) {

    }

    @Override
    public void setTransformationInterpolationStartDelta(int i) {

    }

    @Override
    public void setScale(Vec vec) {

    }

    @Override
    public void setRightRotation(float[] floats) {

    }

    @Override
    public void setTranslation(Pos position) {

    }

    @Override
    public void setTransformationInterpolationDuration(int i) {

    }

    @Override
    public void setPosRotInterpolationDuration(int i) {

    }

    @Override
    public void setViewRange(int i) {

    }

    @Override
    public void setFixedContext() {

    }
}
