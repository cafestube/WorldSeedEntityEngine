package net.worldseed.multipart.scheduling;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ScheduledTask {

    void cancel();
    
}
