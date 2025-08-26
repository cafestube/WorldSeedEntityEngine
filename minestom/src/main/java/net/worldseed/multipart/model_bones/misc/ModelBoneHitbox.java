package net.worldseed.multipart.model_bones.misc;

import com.google.gson.JsonArray;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.network.packet.server.play.EntityTeleportPacket;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.animations.BoneAnimation;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.model_bones.ModelBoneImpl;
import net.worldseed.multipart.model_bones.BoneEntity;
import net.worldseed.multipart.model_bones.ModelBone;
import net.worldseed.multipart.model_bones.bone_types.HitboxBone;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class ModelBoneHitbox extends ModelBoneImpl<Player> implements HitboxBone<Player> {
    private static final int INTERPOLATE_TICKS = 2;
    private static final Tag<String> WSEE = Tag.String("WSEE");
    private final JsonArray cubes;
    private final Collection<ModelBone<Player>> illegitimateChildren = new ConcurrentLinkedDeque<>();
    private final Point orgPivot;
    private Task positionTask;

    public ModelBoneHitbox(Point pivot, String name, Point rotation, MinestomModel model, Point newOffset, double sizeX, double sizeY, JsonArray cubes, boolean parent, float scale) {
        super(pivot, name, rotation, model, scale);

        this.orgPivot = pivot;
        this.cubes = cubes;

        if (parent) {
            generateStands(cubes, pivot, name, rotation, model);
            this.offset = null;
        } else {
            if (this.offset != null) {
                BoneEntity entity = new BoneEntity(EntityType.INTERACTION, model, name) {
                    @Override
                    public void updateNewViewer(@NotNull Player player) {
                        super.updateNewViewer(player);

                        EntityTeleportPacket packet = new EntityTeleportPacket(this.getEntityId(), this.position, net.minestom.server.coordinate.Vec.ZERO, 0, false);
                        player.getPlayerConnection().sendPacket(packet);
                    }

                    @Override
                    public void updateOldViewer(@NotNull Player player) {
                        super.updateOldViewer(player);
                    }
                };
                this.stand = entity;

                entity.setTag(WSEE, "hitbox");
                this.offset = newOffset;

                InteractionMeta meta = (InteractionMeta) entity.getEntityMeta();
                meta.setHeight((float) (sizeY / 4f) * scale);
                meta.setWidth((float) (sizeX / 4f) * scale);

                entity.setBoundingBox(sizeX / 4f * scale, sizeY / 4f * scale, sizeX / 4f * scale);
            }
        }
    }

    @Override
    public BoneEntity getEntity() {
        return (BoneEntity) super.getEntity();
    }

    public void addViewer(Player player) {
        BoneEntity entity = this.getEntity();
        if (entity != null) entity.addViewer(player);
        illegitimateChildren.forEach(modelBone -> modelBone.addViewer(player));
    }

    public void removeViewer(Player player) {
        BoneEntity entity = this.getEntity();
        if (entity != null) entity.removeViewer(player);
        illegitimateChildren.forEach(modelBone -> modelBone.removeViewer(player));
    }

    @Override
    public void setGlobalScale(float scale) {
        super.setGlobalScale(scale);

        illegitimateChildren.forEach(ModelBone::destroy);
        this.illegitimateChildren.clear();

        generateStands(this.cubes, orgPivot, this.name, this.rotation, (MinestomModel) this.model);
        this.illegitimateChildren.forEach(modelBone -> {
            ((BoneEntity) modelBone.getEntity()).setInstance(((MinestomModel)model).getInstance(), model.getPosition());
            modelBone.setParent(getParent());
            model.getViewers().forEach(modelBone::addViewer);
        });
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
    public void attachModel(GenericModel<Player> model) {
        throw new UnsupportedOperationException("Cannot attach a model to a hitbox");
    }

    @Override
    public List<GenericModel<Player>> getAttachedModels() {
        return List.of();
    }

    @Override
    public void detachModel(GenericModel<Player> model) {
        throw new UnsupportedOperationException("Cannot detach a model from a hitbox");
    }

    @Override
    public void setGlobalRotation(double yaw, double pitch) {
    }

    public void generateStands(JsonArray cubes, Point pivotPos, String name, Point boneRotation, MinestomModel genericModel) {
        for (var cube : cubes) {
            JsonArray sizeArray = cube.getAsJsonObject().get("size").getAsJsonArray();
            JsonArray origin = cube.getAsJsonObject().get("origin").getAsJsonArray();

            Point sizePoint = new Vec(sizeArray.get(0).getAsFloat(), sizeArray.get(1).getAsFloat(), sizeArray.get(2).getAsFloat());
            Point originPoint = new Vec(origin.get(0).getAsFloat(), origin.get(1).getAsFloat(), origin.get(2).getAsFloat());

            double maxSize = Math.max(Math.min(Math.min(sizePoint.x(), sizePoint.y()), sizePoint.z()), 0.5);
            while (maxSize > (16 / scale)) {
                maxSize /= 2;
            }

            var newPoint = originPoint
                    .add(sizePoint.x() / 2, 0, sizePoint.z() / 2)
                    .mul(-1, 1, 1);

            for (int x = 0; x < sizePoint.x() / maxSize; ++x) {
                for (int y = 0; y < sizePoint.y() / maxSize; ++y) {
                    for (int z = 0; z < sizePoint.z() / maxSize; ++z) {
                        var currentPos = new Vec(x * maxSize, y * maxSize, z * maxSize);

                        currentPos = currentPos.sub(sizePoint.x() / 2, 0, sizePoint.z() / 2);
                        currentPos = currentPos.add(maxSize / 2, 0, maxSize / 2);

                        if ((currentPos.x() + maxSize) > sizePoint.x()) {
                            currentPos = currentPos.withX(sizePoint.x() - maxSize);
                        }

                        if ((currentPos.z() + maxSize) > sizePoint.z())
                            currentPos = currentPos.withZ(sizePoint.z() - maxSize);

                        if ((currentPos.y() + maxSize) > sizePoint.y())
                            currentPos = currentPos.withY(sizePoint.y() - maxSize);

                        var created = new ModelBoneHitbox(pivotPos, name, boneRotation, genericModel, currentPos.add(newPoint), maxSize, maxSize, cubes, false, scale);
                        illegitimateChildren.add(created);
                    }
                }
            }
        }
    }

    @Override
    public void setParent(ModelBone<Player> parent) {
        super.setParent(parent);
        this.illegitimateChildren.forEach(modelBone -> modelBone.setParent(parent));
    }

    @Override
    public Point getPosition() {
        return PositionConversion.fromMinestom(getEntity().getPosition());
    }

    @Override
    public @NotNull Collection<ModelBone<Player>> getChildren() {
        if (this.illegitimateChildren == null) return List.of();
        return this.illegitimateChildren;
    }

    @Override
    public void addAnimation(BoneAnimation animation) {
        super.addAnimation(animation);
        this.illegitimateChildren.forEach(modelBone -> modelBone.addAnimation(animation));
    }

    @Override
    public void setState(String state) {
    }

    @Override
    public Pos calculatePosition() {
        if (this.offset == null) return Pos.ZERO;
        Point p = this.offset;

        p = applyTransform(p);
        p = calculateGlobalRotation(p);

        return Pos.fromPoint(p).div(4).mul(scale);
    }

    @Override
    public void destroy() {
        super.destroy();
        illegitimateChildren.forEach(ModelBone::destroy);
    }

    @Override
    public Point calculateRotation() {
        return Vec.ZERO;
    }

    @Override
    public Point calculateScale() {
        return Vec.ZERO;
    }

    @Override
    public void teleport(Point position) {
        draw();
    }

    public void draw() {
        if (!this.illegitimateChildren.isEmpty()) {
            this.children.forEach(ModelBone::draw);
            this.illegitimateChildren.forEach(ModelBone::draw);
        }

        BoneEntity entity = this.getEntity();
        if (this.offset == null || entity == null) return;

        var finalPosition = PositionConversion.asMinestom(calculatePosition().add(model.getPosition()));
        if (this.positionTask != null) this.positionTask.cancel();

        net.minestom.server.coordinate.Pos currentPos = entity.getPosition();
        var diff = finalPosition.sub(currentPos).div(INTERPOLATE_TICKS);
        AtomicInteger ticks = new AtomicInteger(1);

        this.positionTask = MinecraftServer.getSchedulerManager().submitTask(() -> {
            var t = ticks.getAndIncrement();
            if (entity.isRemoved()) return TaskSchedule.stop();

            var newPos = currentPos.add(diff.mul(t));
            if (entity.getDistanceSquared(newPos) > 0.005) entity.teleport(newPos);

            if (t >= INTERPOLATE_TICKS) {
                this.positionTask = null;
                return TaskSchedule.stop();
            }

            return TaskSchedule.tick(1);
        });
    }
}
