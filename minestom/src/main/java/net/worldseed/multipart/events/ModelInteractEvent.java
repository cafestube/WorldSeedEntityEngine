package net.worldseed.multipart.events;

import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.worldseed.multipart.model_bones.MinestomBoneEntity;
import net.worldseed.multipart.gestures.EmoteModel;
import net.worldseed.multipart.MinestomModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModelInteractEvent implements ModelEvent {
    private final MinestomModel model;
    private final Player interactor;
    private final MinestomBoneEntity interactedBone;
    private final PlayerHand hand;

    public ModelInteractEvent(@NotNull EmoteModel model, PlayerEntityInteractEvent event) {
        this(model, event, null);
    }
    
    public ModelInteractEvent(@NotNull MinestomModel model, PlayerEntityInteractEvent event, @Nullable MinestomBoneEntity interactedBone) {
        this.model = model;
        this.hand = event.getHand();
        this.interactor = event.getPlayer();
        this.interactedBone = interactedBone;
    }

    @Override
    public @NotNull MinestomModel model() {
        return model;
    }

    public @NotNull PlayerHand getHand() {
        return hand;
    }

    public @NotNull Player getInteracted() { // This should probably be getInteractor() or getPlayer() but I left this untouched so code doesn't break
        return interactor;
    }
    
    public @Nullable MinestomBoneEntity getBone() {
        return interactedBone;
    }
}

