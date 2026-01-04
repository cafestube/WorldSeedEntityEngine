package net.worldseed.multipart;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.RGBLike;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.animations.ModelAnimation;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.entity.ModelBone;
import net.worldseed.multipart.entity.entity.RootBoneEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GenericModel<TViewer> {
    /**
     * Get the ID of the model
     *
     * @return the model ID
     */
    String getId();

    AnimationHandler getAnimationHandler();

    ModelPlatform<TViewer> getModelPlatform();

    AbstractModelRegistry getModelRegistry();

    /**
     * Get the pivot point of the model. Used for global rotation
     *
     * @return the global rotation pivot point
     */
    Point getPivot();

    /**
     * Get the rotation of the model on the X axis
     *
     * @return the pitch
     */
    double getPitch();

    /**
     * Get the rotation of the model on the Y axis
     *
     * @return the global rotation
     */
    double getGlobalRotation();

    /**
     * Set the rotation of the model on the Y axis
     *
     * @param rotation new global rotation
     */
    void setGlobalRotation(double rotation);

    /**
     * Set the rotation of the model on the Y and X axis
     *
     * @param yaw   new global rotation
     * @param pitch new pitch
     */
    void setGlobalRotation(double yaw, double pitch);

    /**
     * Get the postion offset for drawing the model
     *
     * @return the position
     */
    Point getGlobalOffset();

    /**
     * Get the position the model is being drawn at
     *
     * @return the model position
     */
    Pos getPosition();

    /**
     * Set the position of the model
     *
     * @param pos new model position
     */
    void setPosition(Pos pos);

    /**
     * Set the state of the model. By default, `normal` and `hit` are supported
     *
     * @param state the new state
     */
    void setState(String state);

    /**
     * Destroy the model
     */
    void destroy();

    boolean addViewer(TViewer player);

    boolean removeViewer(TViewer player);

    Set<TViewer> getViewers();

    /**
     * Get a VFX bone location
     *
     * @param name the name of the bone
     * @return the bone location
     */
    Point getVFX(String name);

    @ApiStatus.Internal
    ModelBone<TViewer> getPart(String boneName);

    @ApiStatus.Internal
    void draw();

    /**
     * Set the model's head rotation
     *
     * @param name     name of the bone
     * @param rotation rotation of head
     */
    void setHeadRotation(String name, double rotation);

    @NotNull List<ModelBone<TViewer>> getParts();


    Point getOffset(String bone);

    Point getDiff(String bone);

    void triggerAnimationComplete(ModelAnimation animation, AnimationHandler.AnimationDirection direction);

    void triggerAnimationStopped(ModelAnimation animation, AnimationHandler.AnimationDirection direction, boolean looped);

    void triggerAnimationStart(ModelAnimation animation, AnimationHandler.AnimationDirection direction, short tick, boolean looped);

    void setGlobalScale(float scale);

    void removeGlowing();

    void setGlowing(RGBLike color);

    void removeGlowing(TViewer player);

    void setGlowing(TViewer player, RGBLike color);

    void attachModel(GenericModel<TViewer> model, String boneName);

    Map<String, List<GenericModel<TViewer>>> getAttachedModels();

    void detachModel(GenericModel<TViewer> model, String boneName);

    RootBoneEntity<TViewer> getModelRoot();

    void setNametag(String name, @Nullable Component nametag);

    @Nullable Component getNametag(String name);

    boolean isSpawned();
}
