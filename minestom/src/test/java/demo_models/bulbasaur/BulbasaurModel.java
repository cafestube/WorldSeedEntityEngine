package demo_models.bulbasaur;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.worldseed.multipart.GenericModelImpl;
import net.worldseed.multipart.ModelRegistry;
import net.worldseed.multipart.ModelRegistry;
import net.worldseed.multipart.blueprint.ModelBlueprint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BulbasaurModel extends GenericModelImpl {

    public BulbasaurModel(ModelRegistry registry) {
        super(ModelBlueprint.loadBlueprint("bulbasaur/bulbasaur.bbmodel", registry));
    }

    public void init(@Nullable Instance instance, @NotNull Pos position) {
        super.init(instance, position, 1.0f);
    }
}