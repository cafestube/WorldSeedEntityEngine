package net.worldseed.multipart.entity;

import net.kyori.adventure.util.RGBLike;
import net.worldseed.multipart.GenericModel;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Quaternion;
import net.worldseed.multipart.animations.BoneAnimation;
import net.worldseed.multipart.entity.entity.BoneEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

@ApiStatus.Internal
public interface ModelBone<TViewer> {

    Point applyTransform(Point p);

    void draw();

    void destroy();

    void setState(String state);

    String getName();

    BoneEntity<TViewer> getEntity();

    Point getOffset();

    Point getPosition();

    ModelBone<TViewer> getParent();

    void setParent(ModelBone<TViewer> parent);

    Point getPropogatedRotation();

    Point getPropogatedScale();

    Point calculateScale();

    Pos calculatePosition();

    Point calculateRotation(Point p, Point rotation, Point pivot);

    Point calculateScale(Point p, Point scale, Point pivot);

    Point calculateRotation();

    Point calculateFinalScale(Point p);

    Quaternion calculateFinalAngle(Quaternion q);

    void addChild(ModelBone<TViewer> child);

    void addAnimation(BoneAnimation animation);

    void addViewer(TViewer player);

    void removeViewer(TViewer player);

    void setGlobalScale(float scale);

    void removeGlowing();

    void setGlowing(RGBLike color);

    void removeGlowing(TViewer player);

    void setGlowing(TViewer player, RGBLike color);

    void attachModel(GenericModel<TViewer> model);

    List<GenericModel<TViewer>> getAttachedModels();

    void detachModel(GenericModel<TViewer> model);

    void setGlobalRotation(double yaw, double pitch);

    default void teleport(Point position) {}

    default @NotNull Collection<ModelBone<TViewer>> getChildren() {
        return List.of();
    };
}
