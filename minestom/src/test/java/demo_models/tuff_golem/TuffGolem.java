package demo_models.tuff_golem;

import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.worldseed.multipart.GenericModelImpl;
import net.worldseed.multipart.ModelRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TuffGolem extends GenericModelImpl {
    public TuffGolem(ModelRegistry registry) {
        super(registry, "tuff_golem.bbmodel");
    }

    public void init(@Nullable Instance instance, @NotNull Pos position) {
        super.init(instance, position);
    }
}
