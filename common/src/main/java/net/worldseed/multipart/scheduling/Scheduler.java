package net.worldseed.multipart.scheduling;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface Scheduler {

    ScheduledTask syncRepeating(@NotNull Runnable task, long delay, long interval);

}
