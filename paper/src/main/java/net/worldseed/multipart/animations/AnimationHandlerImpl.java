package net.worldseed.multipart.animations;


import net.worldseed.multipart.PaperModel;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class AnimationHandlerImpl extends AbstractAnimationHandlerImpl {

    private final BukkitTask task;

    public AnimationHandlerImpl(PaperModel model, JavaPlugin plugin) {
        super(model);
        this.task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 0, 1);
    }

    @Override
    public void destroy() {
        this.task.cancel();
    }
}
