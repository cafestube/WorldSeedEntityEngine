package net.worldseed.multipart;

import net.worldseed.multipart.scheduling.ScheduledTask;
import net.worldseed.multipart.scheduling.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class PaperScheduler implements Scheduler {

    private final JavaPlugin plugin;

    public PaperScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ScheduledTask syncRepeating(@NotNull Runnable task, long delay, long interval) {
        return new PaperScheduledTask(Bukkit.getScheduler().runTaskTimer(plugin, task, delay, interval));
    }

    record PaperScheduledTask(BukkitTask task)  implements ScheduledTask {
        @Override
        public void cancel() {
            task.cancel();
        }
    }
}
