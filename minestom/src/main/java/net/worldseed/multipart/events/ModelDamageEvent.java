package net.worldseed.multipart.events;

import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.sound.SoundEvent;
import net.worldseed.multipart.MinestomModel;
import net.worldseed.multipart.model_bones.MinestomBoneEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModelDamageEvent implements ModelEvent, CancellableEvent {
    private final MinestomModel model;
    private final MinestomBoneEntity hitBone;
    private final Damage damage;
    private SoundEvent sound;

    private boolean animation = true;

    private boolean cancelled;

    public ModelDamageEvent(MinestomModel model, EntityDamageEvent event) {
        this(model, event, null);
    }
    
    public ModelDamageEvent(MinestomModel model, EntityDamageEvent event, @Nullable MinestomBoneEntity hitBone) {
        this.model = model;
        this.hitBone = hitBone;
        this.damage = event.getDamage();
        this.sound = event.getSound();
        this.animation = event.shouldAnimate();
    }

    /**
     * Gets the damage.
     *
     * @return the damage
     */
    @NotNull
    public Damage getDamage() {
        return damage;
    }

    /**
     * Gets the damage sound.
     *
     * @return the damage sound
     */
    @Nullable
    public SoundEvent getSound() {
        return sound;
    }

    /**
     * Changes the damage sound.
     *
     * @param sound the new damage sound
     */
    public void setSound(@Nullable SoundEvent sound) {
        this.sound = sound;
    }

    /**
     * Gets whether the damage animation should be played.
     *
     * @return true if the animation should be played
     */
    public boolean shouldAnimate() {
        return animation;
    }

    /**
     * Sets whether the damage animation should be played.
     *
     * @param animation whether the animation should be played or not
     */
    public void setAnimation(boolean animation) {
        this.animation = animation;
    }

    /**
     * Gets the hitbox bone that has been hit.
     *
     * @return the hitbox bone that has been hit, or null if it was an EmotePlayer
     */
    public @Nullable MinestomBoneEntity getBone() {
        return hitBone;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public MinestomModel model() {
        return model;
    }
}
