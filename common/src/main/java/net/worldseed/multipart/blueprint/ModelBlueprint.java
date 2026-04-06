package net.worldseed.multipart.blueprint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.key.Key;
import net.worldseed.multipart.AbstractModelRegistry;
import net.worldseed.multipart.blueprint.animation.AnimationData;
import net.worldseed.multipart.entity.ModelBone;
import net.worldseed.multipart.entity.display_entity.ModelBonePartDisplay;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.PositionParser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public record ModelBlueprint(
        Key modelId,
        Map<String, ModelBoneInfo> parts,
        Map<String, AnimationData> animations
) {

    public ModelBlueprint remappedWith(ModelBlueprint other) {
        Map<String, ModelBoneInfo> parts = new HashMap<>();

        for (ModelBoneInfo value : this.parts.values()) {
            ModelBoneInfo info = other.parts.get(value.name());

            if(info != null) {
                parts.put(value.name(), value.withRenderInfo(info.renderInfo()));
            } else {
                parts.put(value.name(), value);
            }


        }

        return new ModelBlueprint(modelId, parts, animations);
    }

    public static ModelBlueprint loadBlueprint(String model, AbstractModelRegistry modelRegistry) {
        final LinkedHashMap<String, ModelBoneInfo> parts = new LinkedHashMap<>();
        final Map<String, AnimationData> animations = modelRegistry.getOrLoadAnimations(model);
        final JsonObject loadedModel = modelRegistry.getOrLoadGeometry(model);

        // Build bones
        for (JsonElement bone : loadedModel.get("minecraft:geometry").getAsJsonArray().get(0).getAsJsonObject().get("bones").getAsJsonArray()) {
            JsonElement pivot = bone.getAsJsonObject().get("pivot");
            String name = bone.getAsJsonObject().get("name").getAsString();
            JsonElement parent = bone.getAsJsonObject().get("parent");
            String parentString = parent == null ? null : parent.getAsString();

            Point boneRotation = PositionParser.getPos(bone.getAsJsonObject().get("rotation")).orElse(Pos.ZERO).mul(-1, -1, 1);
            Point pivotPos = PositionParser.getPos(pivot).orElse(Pos.ZERO).mul(-1, 1, 1);


            ModelBoneInfo info = new ModelBoneInfo(
                    name,
                    parentString,
                    pivotPos,
                    boneRotation,
                    modelRegistry.getDiffMapping(model, name),
                    modelRegistry.getOffsetMapping(model, name),
                    modelRegistry.getModelRenderInfo(model, name),
                    bone.getAsJsonObject().getAsJsonArray("cubes")
            );
            parts.put(name, info);
        }

        return new ModelBlueprint(Key.key(modelRegistry.getNamespace(), model), parts, animations);
    }

}
