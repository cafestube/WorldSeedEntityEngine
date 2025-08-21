package net.worldseed.multipart.data;

import com.google.gson.JsonObject;
import net.kyori.adventure.key.Key;

import java.io.IOException;
import java.nio.file.Path;

public interface ModelProvider {

    JsonObject loadMappingData(String namespace) throws IOException;

    JsonObject loadGeometry(Key modelId) throws IOException;

    JsonObject loadAnimations(Key modelId) throws IOException;

    static ModelProvider file(Path mappings, Path modelFolder) {
        return new FileModelProvider(mappings, modelFolder);
    }

}
