package net.worldseed.paper.test;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.worldseed.multipart.ModelRegistry;
import net.worldseed.multipart.PositionConversion;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.animations.AnimationHandlerImpl;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnCommand implements BasicCommand {

    private final ModelRegistry registry;

    public SpawnCommand(ModelRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        BulbasaurModel model = new BulbasaurModel(registry);
        model.init(commandSourceStack.getExecutor().getLocation());

        model.addViewer((Player) commandSourceStack.getExecutor());
        AnimationHandler animationHandler = new AnimationHandlerImpl<>(model);
        animationHandler.playRepeat("animation.bulbasaur.faint");
    }
}
