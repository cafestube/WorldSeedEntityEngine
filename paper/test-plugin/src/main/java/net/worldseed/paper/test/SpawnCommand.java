package net.worldseed.paper.test;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.worldseed.multipart.ModelEngine;
import net.worldseed.multipart.PaperModel;
import net.worldseed.multipart.tracker.TrackingRule;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class SpawnCommand implements BasicCommand {

    private final ModelEngine engine;

    public SpawnCommand(ModelEngine engine) {
        this.engine = engine;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String @NonNull [] args) {
        PaperModel model = PaperModel.model(engine, "bulbasaur/bulbasaur.bbmodel");

        model.init(Objects.requireNonNull(commandSourceStack.getExecutor()).getLocation());
        model.getAnimationHandler().playRepeat("animation.bulbasaur.faint");

        engine.getModelTracker().startTracking(model, TrackingRule.always());
    }
}
