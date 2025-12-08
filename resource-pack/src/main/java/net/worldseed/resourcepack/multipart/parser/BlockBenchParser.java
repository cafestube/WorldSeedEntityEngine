package net.worldseed.resourcepack.multipart.parser;

import net.worldseed.resourcepack.math.Vec;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.*;

public class BlockBenchParser {


    public interface OutlinerChild {}

    public record CubeRef(
            String uuid
    ) implements OutlinerChild {}

    public record ModelOutliner(
            ModelGroup group,
            List<OutlinerChild> children
    ) implements OutlinerChild {}

    public record ModelGroup(
            String name,
            UUID uuid,
            Vec origin,
            Vec rotation
    ) {}

    public static List<OutlinerChild> parseModelOutliners(JsonObject model) {
        float scale = 0.25f;

        List<OutlinerChild> children = new ArrayList<>();

        if(model.containsKey("groups")) {
            //BB 5.0 - maybe explicit check?

            Map<UUID, ModelGroup> groups = new HashMap<>();
            for(var groupEl : model.getJsonArray("groups")) {
                JsonObject obj = groupEl.asJsonObject();

                String name = obj.getString("name");
                JsonArray pivotArr = obj.getJsonArray("origin");

                Vec pivot = new Vec(
                        -pivotArr.getJsonNumber(0).doubleValue() * scale,
                        pivotArr.getJsonNumber(1).doubleValue() * scale,
                        pivotArr.getJsonNumber(2).doubleValue() * scale
                );

                Vec rotation = new Vec(0,0,0);

                JsonArray rotationArr = obj.getJsonArray("rotation");
                if (rotationArr != null) {
                    rotation = new Vec(
                            -rotationArr.getJsonNumber(0).doubleValue(),
                            -rotationArr.getJsonNumber(1).doubleValue(),
                            rotationArr.getJsonNumber(2).doubleValue()
                    );
                }

                UUID uuid = UUID.fromString(obj.getString("uuid"));

                ModelGroup group = new ModelGroup(name, uuid, pivot, rotation);
                groups.put(uuid, group);
            }

            for(var outlinerElement : model.getJsonArray("outliner")) {
                children.add(parseBlockBenchV5OutlinerRecursive(outlinerElement, groups));
            }
        } else {
            for(var outlinerElement : model.getJsonArray("outliner")) {
                if (outlinerElement instanceof JsonObject) {
                    children.add(parseBlockBenchV4OutlinerRecursive(outlinerElement.asJsonObject(), scale));
                }
            }
        }
        return children;
    }

    private static OutlinerChild parseBlockBenchV4OutlinerRecursive(JsonObject obj, float scale) {
        String name = obj.getString("name");

        JsonArray pivotArr = obj.getJsonArray("origin");
        Vec pivot = new Vec(
                -pivotArr.getJsonNumber(0).doubleValue() * scale,
                pivotArr.getJsonNumber(1).doubleValue() * scale,
                pivotArr.getJsonNumber(2).doubleValue() * scale
        );

        Vec rotation = new Vec(0,0,0);

        JsonArray rotationArr = obj.getJsonArray("rotation");
        if (rotationArr != null) {
            rotation = new Vec(
                    -rotationArr.getJsonNumber(0).doubleValue(),
                    -rotationArr.getJsonNumber(1).doubleValue(),
                    rotationArr.getJsonNumber(2).doubleValue()
            );
        }

        UUID uuid = UUID.fromString(obj.getString("uuid"));
        List<OutlinerChild> children = new ArrayList<>();

        for (var childEl : obj.getJsonArray("children")) {
            if (childEl.getValueType() == JsonValue.ValueType.OBJECT) {
                children.add(parseBlockBenchV4OutlinerRecursive(childEl.asJsonObject(), scale));
            } else if (childEl.getValueType() == JsonValue.ValueType.STRING) {
                children.add(new CubeRef(childEl.toString()));
            }
        }

        return new ModelOutliner(new ModelGroup(name, uuid, pivot, rotation), children);
    }

    private static OutlinerChild parseBlockBenchV5OutlinerRecursive(JsonValue el, Map<UUID, ModelGroup> groups) {
        if(el.getValueType().equals(JsonValue.ValueType.OBJECT)) {
            UUID groupUuid = UUID.fromString(el.asJsonObject().getString("uuid"));

            List<OutlinerChild> children = new ArrayList<>();
            if(el.asJsonObject().containsKey("children")) {
                for(var childEl : el.asJsonObject().getJsonArray("children")) {
                    children.add(parseBlockBenchV5OutlinerRecursive(childEl, groups));
                }
            }

            return new ModelOutliner(
                groups.get(groupUuid),
                children
            );
        } else if(el.getValueType().equals(JsonValue.ValueType.STRING)) {
            return new CubeRef(el.toString());
        }
        throw new IllegalArgumentException("Unknown outliner element type: " + el.getValueType());
    }



}
