package net.worldseed.multipart.animations;

import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import net.worldseed.multipart.MinestomModel;

public class AnimationHandlerImpl extends AbstractAnimationHandlerImpl {

    private final Task task;

    public AnimationHandlerImpl(MinestomModel model) {
        super(model);
        this.task = MinecraftServer.getSchedulerManager().scheduleTask(this::tick, TaskSchedule.immediate(), TaskSchedule.tick(1), ExecutionType.TICK_START);
    }

    @Override
    public void destroy() {
        this.task.cancel();
    }
}
