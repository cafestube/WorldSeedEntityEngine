package net.worldseed.paper.test;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.worldseed.multipart.ModelEngine;
import net.worldseed.multipart.ModelRegistry;
import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.tracker.TrackingRule;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class SpawnCommand implements BasicCommand {

    private final ModelEngine engine;
    private final ModelRegistry registry;

    public SpawnCommand(ModelEngine engine, ModelRegistry registry) {
        this.engine = engine;
        this.registry = registry;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String @NonNull [] args) {
        PaperModel model = PaperModel.model(registry, "bulbasaur/bulbasaur.bbmodel", engine.getPlugin());

        model.init(Objects.requireNonNull(commandSourceStack.getExecutor()).getLocation());
        model.getAnimationHandler().playRepeat("animation.bulbasaur.faint");

        engine.getModelTracker().startTracking(model, TrackingRule.always());
    }
}
