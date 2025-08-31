package net.worldseed.multipart.entity.misc;

import com.google.gson.JsonArray;
import net.kyori.adventure.util.RGBLike;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.animations.BoneAnimation;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.entity.ModelBoneImpl;
import net.worldseed.multipart.entity.ModelBone;
import net.worldseed.multipart.entity.bone_types.HitboxBone;
import net.worldseed.multipart.entity.entity.BoneEntity;
import net.worldseed.multipart.entity.entity.HitboxEntity;
import net.worldseed.multipart.scheduling.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class ModelBoneHitbox<TViewer> extends ModelBoneImpl<TViewer> implements HitboxBone<TViewer> {
    private static final int INTERPOLATE_TICKS = 2;
    private final JsonArray cubes;
    private final Collection<ModelBone<TViewer>> illegitimateChildren = new ConcurrentLinkedDeque<>();
    private final Point orgPivot;
    private ScheduledTask positionTask;

    public ModelBoneHitbox(Point pivot, String name, Point rotation, GenericModel<TViewer> model, Point newOffset, double sizeX, double sizeY, JsonArray cubes, boolean parent, float scale) {
        super(pivot, name, rotation, model, scale);

        this.orgPivot = pivot;
        this.cubes = cubes;

        if (parent) {
            generateStands(cubes, pivot, name, rotation, model);
            this.offset = null;
        } else {
            if (this.offset != null) {
                HitboxEntity<TViewer> entity = model.getModelPlatform().createHitboxEntity(model, name);
                entity.setSize(sizeX / 4f * scale, sizeY / 4f * scale);
                this.stand = entity;
                this.offset = newOffset;
            }
        }
    }

    public void addViewer(TViewer player) {
        BoneEntity<TViewer> entity = this.getEntity();
        if (entity != null) entity.addViewer(player);
        illegitimateChildren.forEach(modelBone -> modelBone.addViewer(player));
    }

    public void removeViewer(TViewer player) {
        BoneEntity<TViewer> entity = this.getEntity();
        if (entity != null) entity.removeViewer(player);
        illegitimateChildren.forEach(modelBone -> modelBone.removeViewer(player));
    }

    @Override
    public void setGlobalScale(float scale) {
        super.setGlobalScale(scale);

        illegitimateChildren.forEach(ModelBone::destroy);
        this.illegitimateChildren.clear();

        generateStands(this.cubes, orgPivot, this.name, this.rotation, this.model);
        this.illegitimateChildren.forEach(modelBone -> {
            model.getModelPlatform().spawn(this.model, modelBone.getEntity(), model.getPosition());
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
    public void removeGlowing(TViewer player) {
    }

    @Override
    public void setGlowing(TViewer player, RGBLike color) {
    }

    @Override
    public void attachModel(GenericModel<TViewer> model) {
        throw new UnsupportedOperationException("Cannot attach a model to a hitbox");
    }

    @Override
    public List<GenericModel<TViewer>> getAttachedModels() {
        return List.of();
    }

    @Override
    public void detachModel(GenericModel<TViewer> model) {
        throw new UnsupportedOperationException("Cannot detach a model from a hitbox");
    }

    @Override
    public void setGlobalRotation(double yaw, double pitch) {
    }

    public void generateStands(JsonArray cubes, Point pivotPos, String name, Point boneRotation, GenericModel<TViewer> genericModel) {
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

                        var created = new ModelBoneHitbox<>(pivotPos, name, boneRotation, genericModel, currentPos.add(newPoint), maxSize, maxSize, cubes, false, scale);
                        illegitimateChildren.add(created);
                    }
                }
            }
        }
    }

    @Override
    public void setParent(ModelBone<TViewer> parent) {
        super.setParent(parent);
        this.illegitimateChildren.forEach(modelBone -> modelBone.setParent(parent));
    }

    @Override
    public Point getPosition() {
        return getEntity().getLocation();
    }

    @Override
    public @NotNull Collection<ModelBone<TViewer>> getChildren() {
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

        BoneEntity<TViewer> entity = this.getEntity();
        if (this.offset == null || entity == null) return;

        var finalPosition = calculatePosition().add(model.getPosition());
        if (this.positionTask != null) this.positionTask.cancel();

        Pos currentPos = entity.getLocation();
        var diff = finalPosition.sub(currentPos).div(INTERPOLATE_TICKS);
        AtomicInteger ticks = new AtomicInteger(1);

        this.positionTask = this.model.getModelPlatform().getScheduler(this.model).syncRepeating(() -> {
            var t = ticks.getAndIncrement();
            if (entity.isRemoved()) {
                this.positionTask.cancel();
                this.positionTask = null;
                return;
            }

            var newPos = currentPos.add(diff.mul(t));
            if (entity.getDistanceSquared(newPos) > 0.005) entity.teleport(newPos);

            if (t >= INTERPOLATE_TICKS) {
                this.positionTask.cancel();
                this.positionTask = null;
            }
        }, 0, 1);
    }
}
