package demo_models;

import net.worldseed.multipart.data.ModelProvider;
import net.worldseed.multipart.ModelRegistry;

import java.nio.file.Path;

public class Registry {

    private static final Path BASE_PATH = Path.of("minestom/src/test/resources");
    private static final Path MODEL_PATH = BASE_PATH.resolve("models");
    public static final ModelRegistry REGISTRY = new ModelRegistry("worldseed", ModelProvider.file(BASE_PATH.resolve("model_mappings.json"), MODEL_PATH));

}
