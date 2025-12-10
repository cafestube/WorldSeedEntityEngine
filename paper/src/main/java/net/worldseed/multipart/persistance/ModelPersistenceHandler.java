package net.worldseed.multipart.persistance;

import net.worldseed.multipart.PaperModel;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface ModelPersistenceHandler {

    @NotNull CompletableFuture<PaperModel> determineModel(final @NotNull Entity entity);

    void saveModel(final @NotNull Entity entity, final @NotNull PaperModel model);

}
