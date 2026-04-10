package net.worldseed.multipart;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.RGBLike;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.animations.ModelAnimationInstance;
import net.worldseed.multipart.blueprint.ModelBlueprint;
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

    ModelBlueprint getBlueprint();

    void changePart(String part, String newPart, ModelBlueprint newModel);

    void remapModel(ModelBlueprint newModel);

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
     * @deprecated Confusing name
     * @return the global rotation
     */
    @Deprecated
    default double getGlobalRotation() {
        return getGlobalYRotation();
    }

    double getGlobalYRotation();

    /**
     * Set the rotation of the model on the Y axis
     *
     * @param yaw new global rotation
     */
    void setGlobalRotation(double yaw);

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
     * @return the translation
     */
    Point getGlobalOffset();

    /**
     * Get the translation the model is being drawn at
     *
     * @return the model translation
     */
    Pos getPosition();

    /**
     * Set the translation of the model
     *
     * @param pos new model translation
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
     * @param yaw      rotation of head
     */
    void setHeadRotation(String name, double yaw);

    /**
     * Set the model's head rotation
     *
     * @param name     name of the bone
     * @param yaw      rotation of head
     * @param pitch    rotation of head
     */
    void setHeadRotation(String name, double yaw, double pitch);

    void setHeadRotation(double yaw, double pitch);

    @NotNull List<ModelBone<TViewer>> getParts();

    void triggerAnimationComplete(ModelAnimationInstance animation, AnimationHandler.AnimationDirection direction);

    void triggerAnimationStopped(ModelAnimationInstance animation, AnimationHandler.AnimationDirection direction, boolean looped);

    void triggerAnimationStart(ModelAnimationInstance animation, AnimationHandler.AnimationDirection direction, short tick, boolean looped);

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
