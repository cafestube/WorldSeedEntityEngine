package net.worldseed.multipart.model_bones;

import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.model_bones.entity.BoneEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PaperPacketBoneEntity implements BoneEntity<Player> {
    private final PaperModel model;
    private final String name;

    public PaperPacketBoneEntity(@NotNull EntityType entityType, PaperModel model, String name) {
        this.model = model;
        this.name = name;

    }

    public PaperModel getModel() {
        return model;
    }

    public String getName() {
        return name;
    }


    @Override
    public void remove() {

    }

    @Override
    public boolean addViewer(Player player) {
        return false;
    }

    @Override
    public boolean removeViewer(Player player) {
        return false;
    }

    @Override
    public void setGlowing(boolean b) {

    }

    @Override
    public void setGlowing(Player player, boolean b) {

    }

    @Override
    public void setRotation(float yaw, float pitch) {

    }

    @Override
    public void teleport(Pos pos) {

    }
}
