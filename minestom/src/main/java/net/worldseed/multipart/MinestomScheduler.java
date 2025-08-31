package net.worldseed.multipart;

import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import net.worldseed.multipart.scheduling.ScheduledTask;
import net.worldseed.multipart.scheduling.Scheduler;
import org.jetbrains.annotations.NotNull;

public class MinestomScheduler implements Scheduler {

    @Override
    public ScheduledTask syncRepeating(@NotNull Runnable task, long delay, long interval) {
        TaskSchedule delaySchedule = TaskSchedule.immediate();
        TaskSchedule intervalSchedule = TaskSchedule.immediate();

        if(delay > 0) {
            delaySchedule = TaskSchedule.tick((int) delay);
        }

        if(interval > 0) {
            intervalSchedule = TaskSchedule.tick((int) interval);
        }

        return new MinestomScheduledTask(MinecraftServer.getSchedulerManager().scheduleTask(task, delaySchedule, intervalSchedule));
    }

    record MinestomScheduledTask(Task task) implements ScheduledTask {
        @Override
        public void cancel() {
            task.cancel();
        }
    }
}
