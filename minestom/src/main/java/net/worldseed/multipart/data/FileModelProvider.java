package net.worldseed.multipart.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.key.Key;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public record FileModelProvider(Path mappingsPath, Path modelPath) implements ModelProvider {

    @Override
    public JsonObject loadMappingData(String namespace) throws IOException {
        Reader mappingsData = new InputStreamReader(Files.newInputStream(mappingsPath));
        return JsonParser.parseReader(mappingsData).getAsJsonObject();
    }

    @Override
    public JsonObject loadGeometry(Key modelId) throws IOException {
        Reader mappingsData = new InputStreamReader(Files.newInputStream(modelPath.resolve(modelId.value()).resolve("model.geo.json")));
        return JsonParser.parseReader(mappingsData).getAsJsonObject();
    }

    @Override
    public JsonObject loadAnimations(Key modelId) throws IOException {
        Reader mappingsData = new InputStreamReader(Files.newInputStream(modelPath.resolve(modelId.value()).resolve("model.animation.json")));
        return JsonParser.parseReader(mappingsData).getAsJsonObject();
    }

}