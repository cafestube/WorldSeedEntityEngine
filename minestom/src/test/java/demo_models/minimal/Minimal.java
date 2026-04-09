package demo_models.minimal;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.worldseed.multipart.GenericModelImpl;
import net.worldseed.multipart.ModelRegistry;
import net.worldseed.multipart.blueprint.ModelBlueprint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Minimal extends GenericModelImpl {

    public Minimal(ModelRegistry registry) {
        super(ModelBlueprint.loadBlueprint("steve.bbmodel", registry));
    }

    public void init(@Nullable Instance instance, @NotNull Pos position) {
        super.init(instance, position, 2.5f);
    }
}