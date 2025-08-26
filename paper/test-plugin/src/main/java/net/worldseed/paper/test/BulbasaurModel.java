package net.worldseed.paper.test;

import net.worldseed.multipart.GenericModelImpl;
import net.worldseed.multipart.ModelRegistry;
import net.worldseed.multipart.math.Pos;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BulbasaurModel extends GenericModelImpl {

    public BulbasaurModel(ModelRegistry registry) {
        super(registry, "bulbasaur/bulbasaur.bbmodel");
    }

    public void init(@Nullable World instance, @NotNull Pos position) {
        super.init(instance, position, 1.0f);
    }
}