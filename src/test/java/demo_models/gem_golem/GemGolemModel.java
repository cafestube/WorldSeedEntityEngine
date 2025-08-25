package demo_models.gem_golem;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.worldseed.multipart.GenericModelImpl;
import net.worldseed.multipart.ModelRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GemGolemModel extends GenericModelImpl {

    public GemGolemModel(ModelRegistry registry) {
        super(registry, "gem_golem.bbmodel");
    }

    public void init(@Nullable Instance instance, @NotNull Pos position, Component nametag) {
        super.init(instance, position, 3.5f);
        setNametag("nametag", nametag);
    }
}