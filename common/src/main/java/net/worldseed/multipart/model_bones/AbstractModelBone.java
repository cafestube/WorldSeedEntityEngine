package net.worldseed.multipart.model_bones;

import net.kyori.adventure.util.RGBLike;
import net.worldseed.multipart.AbstractGenericModel;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Quaternion;
import net.worldseed.multipart.animations.BoneAnimation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

@ApiStatus.Internal
public interface AbstractModelBone<TViewer, TModel extends AbstractGenericModel<TViewer, ?, ?>, TBone extends AbstractModelBone<TViewer, TModel, TBone>> {
//    CompletableFuture<Void> spawn(Instance instance, Pos position);

    Point applyTransform(Point p);

    void draw();

    void destroy();

    void setState(String state);

    String getName();

    AbstractBoneEntity getEntity();

    Point getOffset();

    Point getPosition();

    TBone getParent();

    void setParent(TBone parent);

    Point getPropogatedRotation();

    Point getPropogatedScale();

    Point calculateScale();

    Pos calculatePosition();

    Point calculateRotation(Point p, Point rotation, Point pivot);

    Point calculateScale(Point p, Point scale, Point pivot);

    Point calculateRotation();

    Point calculateFinalScale(Point p);

    Quaternion calculateFinalAngle(Quaternion q);

    void addChild(TBone child);

    void addAnimation(BoneAnimation animation);

    void addViewer(TViewer player);

    void removeViewer(TViewer player);

    void setGlobalScale(float scale);

    void removeGlowing();

    void setGlowing(RGBLike color);

    void removeGlowing(TViewer player);

    void setGlowing(TViewer player, RGBLike color);

    void attachModel(TModel model);

    List<TModel> getAttachedModels();

    void detachModel(TModel model);

    void setGlobalRotation(double yaw, double pitch);

    default void teleport(Point position) {}

    default @NotNull Collection<TBone> getChildren() {
        return List.of();
    };
}
