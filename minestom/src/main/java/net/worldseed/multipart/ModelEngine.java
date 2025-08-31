package net.worldseed.multipart;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientInputPacket;
import net.worldseed.multipart.events.ModelControlEvent;
import net.worldseed.multipart.events.ModelDamageEvent;
import net.worldseed.multipart.events.ModelInteractEvent;
import net.worldseed.multipart.entity.MinestomBoneEntity;

public class ModelEngine {
    private static final EventListener<PlayerPacketEvent> playerListener = EventListener.of(PlayerPacketEvent.class, event -> {
        if (event.getPacket() instanceof ClientInputPacket packet) {
            Entity ridingEntity = event.getPlayer().getVehicle();

            if (ridingEntity instanceof MinestomBoneEntity bone) {
                EventDispatcher.call(new ModelControlEvent(bone.getModel(), packet));
            }
        }
    });
    private static final EventListener<PlayerEntityInteractEvent> playerInteractListener = EventListener.of(PlayerEntityInteractEvent.class, event -> {
        if (event.getTarget() instanceof MinestomBoneEntity bone) {
            ModelInteractEvent modelInteractEvent = new ModelInteractEvent(bone.getModel(), event, bone);
            EventDispatcher.call(modelInteractEvent);
        }
    });
    private static final EventListener<EntityDamageEvent> entityDamageListener = EventListener.of(EntityDamageEvent.class, event -> {
        if (event.getEntity() instanceof MinestomBoneEntity bone) {
            event.setCancelled(true);
            ModelDamageEvent modelDamageEvent = new ModelDamageEvent(bone.getModel(), event, bone);
            MinecraftServer.getGlobalEventHandler().call(modelDamageEvent);
        }
    });

    /**
     * Registers the event listeners for the Model Engine.
     */
    public static void registerListener() {
        MinecraftServer.getGlobalEventHandler()
                .addListener(playerListener)
                .addListener(playerInteractListener)
                .addListener(entityDamageListener);
    }




}
