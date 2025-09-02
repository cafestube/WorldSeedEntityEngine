package net.worldseed.paper.test.profiler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToByteEncoder;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/*
 * Original Author: toxicity188
 * https://github.com/toxicity188/BetterModel/wiki/Report-about-network-cost-about-two-model-plugin-(ModelEngine,-BetterModel)
 * BetterModel is licensed under the MIT License.
 */
public class ModelProfiler {

    private static final DecimalFormat FORMAT = new DecimalFormat("#,###");
    private static final String VANILLA_ENCODER = "encoder";
    private static final Method ENCODE_METHOD;

    static {
        try {
            ENCODE_METHOD = MessageToByteEncoder.class
                    .getDeclaredMethod("encode", ChannelHandlerContext.class, Object.class, ByteBuf.class);
            ENCODE_METHOD.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final JavaPlugin plugin;
    private final Map<UUID, PlayerProfiler> profilerMap = new ConcurrentHashMap<>();

    public ModelProfiler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void join(PlayerJoinEvent event) {
                var player = event.getPlayer();
                var channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
                channel.eventLoop().submit(() -> profilerMap.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerProfiler(player, channel.pipeline())));
            }
            @EventHandler
            public void quit(PlayerQuitEvent event) {
                var handler = profilerMap.remove(event.getPlayer().getUniqueId());
                if (handler != null) handler.remove();
            }
        }, this.plugin);
    }

    private record TimedPacketSize(long time, int bytes) {}

    private class PlayerProfiler extends MessageToByteEncoder<Packet<?>> {
        private final Queue<TimedPacketSize> byteQueue = new ConcurrentLinkedQueue<>();
        private final ScheduledTask actionbar;
        private final MessageToByteEncoder<?> delegate;

        private PlayerProfiler(@NotNull Player player, @NotNull ChannelPipeline pipeline) {
            actionbar = Bukkit.getAsyncScheduler().runAtFixedRate(ModelProfiler.this.plugin, task -> {
                var time = System.currentTimeMillis();
                byteQueue.removeIf(t -> time - t.time > 1000);
                if (!byteQueue.isEmpty()) {
                    player.sendActionBar(Component.text("Current traffic usage: " + FORMAT.format(byteQueue.stream()
                            .mapToInt(i -> i.bytes)
                            .sum() / 1024) + " kB/s"));
                }
            }, 50, 50, TimeUnit.MILLISECONDS);
            delegate = (MessageToByteEncoder<?>) pipeline.replace(VANILLA_ENCODER, VANILLA_ENCODER, this);
        }

        private void remove() {
            actionbar.cancel();
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, Packet<?> msg, ByteBuf out) throws Exception {
            var before = out.writerIndex();
            ENCODE_METHOD.invoke(delegate, ctx, msg, out);
            if (msg instanceof ClientboundSetEntityDataPacket) {
                byteQueue.add(new TimedPacketSize(System.currentTimeMillis(), out.writerIndex() - before));
            }
        }
    }

}
