package net.worldseed.multipart;

import com.google.gson.*;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
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
import net.worldseed.multipart.model_bones.BoneEntity;
import net.worldseed.multipart.mql.MQLPoint;

import javax.json.JsonNumber;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class ModelEngine {
    private static final EventListener<PlayerPacketEvent> playerListener = EventListener.of(PlayerPacketEvent.class, event -> {
        if (event.getPacket() instanceof ClientInputPacket packet) {
            Entity ridingEntity = event.getPlayer().getVehicle();

            if (ridingEntity instanceof BoneEntity bone) {
                EventDispatcher.call(new ModelControlEvent(bone.getModel(), packet));
            }
        }
    });
    private static final EventListener<PlayerEntityInteractEvent> playerInteractListener = EventListener.of(PlayerEntityInteractEvent.class, event -> {
        if (event.getTarget() instanceof BoneEntity bone) {
            ModelInteractEvent modelInteractEvent = new ModelInteractEvent(bone.getModel(), event, bone);
            EventDispatcher.call(modelInteractEvent);
        }
    });
    private static final EventListener<EntityDamageEvent> entityDamageListener = EventListener.of(EntityDamageEvent.class, event -> {
        if (event.getEntity() instanceof BoneEntity bone) {
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


    public static Optional<Point> getPos(JsonElement pivot) {
        if (pivot == null) return Optional.empty();
        else {
            JsonArray arr = pivot.getAsJsonArray();
            return Optional.of(new Vec(arr.get(0).getAsDouble(), arr.get(1).getAsDouble(), arr.get(2).getAsDouble()));
        }
    }

    public static Optional<MQLPoint> getMQLPos(JsonElement pivot) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (pivot == null) return Optional.empty();
        else if (pivot instanceof JsonObject obj) {
            return Optional.of(new MQLPoint(obj));
        } else if (pivot instanceof JsonNumber num) {
            return Optional.of(new MQLPoint(num.doubleValue(), num.doubleValue(), num.doubleValue()));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<MQLPoint> getMQLPos(JsonArray arr) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (arr == null) return Optional.empty();
        else {
            return Optional.of(new MQLPoint(arr));
        }
    }

}
