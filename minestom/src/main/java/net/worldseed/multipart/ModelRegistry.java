package net.worldseed.multipart;

import com.google.gson.JsonObject;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.CustomModelData;
import net.worldseed.multipart.animations.AnimationLoader;
import net.worldseed.multipart.animations.data.AnimationData;
import net.worldseed.multipart.data.ModelProvider;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.PositionParser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelRegistry implements AbstractModelRegistry {

    private final ModelProvider geometryProvider;
    private final @KeyPattern.Namespace String namespace;

    public final HashMap<String, Point> offsetMappings = new HashMap<>();
    public final HashMap<String, Point> diffMappings = new HashMap<>();
    private final HashMap<String, HashMap<String, ItemStack>> blockMappings = new HashMap<>();

    private final Map<String, Map<String, AnimationData>> loadedAnimations = new HashMap<>();
    private final Map<String, JsonObject> loadedModels = new HashMap<>();


    public ModelRegistry(@KeyPattern.Namespace String namespace, ModelProvider geometryProvider) {
        this.geometryProvider = geometryProvider;
        this.namespace = namespace;

        if(!Key.parseableNamespace(namespace))
            throw new IllegalArgumentException("Invalid namespace: " + namespace);

        loadMappings();
    }

    public String getNamespace() {
        return namespace;
    }

    public HashMap<String, ItemStack> getItems(String model, String name) {
        return blockMappings.get(model + "/" + name);
    }

    public void clearCache() {
        loadedAnimations.clear();
        loadedModels.clear();
    }


    public Map<String, AnimationData> getOrLoadAnimations(@KeyPattern.Value @NotNull String toLoad) {
        if (loadedAnimations.containsKey(toLoad))
            return loadedAnimations.get(toLoad);

        JsonObject loadedAnimations1;

        try {
            loadedAnimations1 = this.geometryProvider.loadAnimations(Key.key(namespace, toLoad));
        } catch (IOException e) {
            e.printStackTrace();
            loadedAnimations1 = null;
        }

        Map<String, AnimationData> data = null;
        if(loadedAnimations1 != null) {
            data = AnimationLoader.parseAnimations(loadedAnimations1);
        }

        loadedAnimations.put(toLoad, data);
        return data;
    }

    public JsonObject getOrLoadGeometry(@KeyPattern.Value @NotNull String id) {
        if (loadedModels.containsKey(id))
            return loadedModels.get(id);

        JsonObject loadedModel1;
        try {
            loadedModel1 = this.geometryProvider.loadGeometry(Key.key(namespace, id));
        } catch (IOException e) {
            e.printStackTrace();
            loadedModel1 = null;
        }

        loadedModels.put(id, loadedModel1);
        return loadedModel1;
    }

    @Override
    public Point getDiffMapping(String model, String boneName) {
        return diffMappings.get(model + "/" + boneName);
    }

    @Override
    public Point getOffsetMapping(String model, String boneName) {
        return offsetMappings.get(model + "/" + boneName);
    }

    private void loadMappings() {
        JsonObject modelData = null;
        try {
            modelData = geometryProvider.loadMappingData(this.namespace);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        modelData.entrySet().forEach(entry -> {
            HashMap<String, ItemStack> keys = new HashMap<>();

            String modelId = entry.getValue().getAsJsonObject().get("model_id").getAsString();

            entry.getValue().getAsJsonObject()
                    .get("id")
                    .getAsJsonObject()
                    .entrySet()
                    .forEach(id -> keys.put(id.getKey(), generateBoneItem(id.getValue().getAsFloat(), modelId)));

            blockMappings.put(entry.getKey(), keys);
            offsetMappings.put(entry.getKey(), PositionParser.getPos(entry.getValue().getAsJsonObject().get("offset").getAsJsonArray()).orElse(Pos.ZERO));
            diffMappings.put(entry.getKey(), PositionParser.getPos(entry.getValue().getAsJsonObject().get("diff").getAsJsonArray()).orElse(Pos.ZERO));
        });
    }

    private ItemStack generateBoneItem(float model_id, String model) {
        return ItemStack.builder(Material.PAPER)
                .set(DataComponents.ITEM_MODEL, Key.key(this.namespace, "bbmodel/" + model).asMinimalString())
                .set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(
                        List.of(model_id),
                        List.of(),
                        List.of(),
                        List.of()
                )).build();
    }
}
