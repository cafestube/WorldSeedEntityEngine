package net.worldseed.resourcepack.multipart.generator;

import net.worldseed.resourcepack.multipart.parser.BlockBenchVersion;

import javax.json.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AnimationGenerator {
    public static JsonObject generate(JsonArray animationRaw, BlockBenchVersion version) {
        JsonObjectBuilder animations = Json.createObjectBuilder();
        if (animationRaw == null) return animations.build();

        for (int i = 0; i < animationRaw.size(); i++) {
            JsonObject animation = animationRaw.getJsonObject(i);

            String name = animation.getString("name");
            double length = animation.getJsonNumber("length").doubleValue();

            JsonObjectBuilder bones = Json.createObjectBuilder();

            var foundAnimations = animation.getJsonObject("animators");
            if (foundAnimations == null) continue;

            Collection<JsonValue> animators = foundAnimations.values();

            for (var animator_ : animators) {
                JsonObject animator = animator_.asJsonObject();

                String type = animator.getString("type", "bone");

                if (!type.equals("bone")) continue;
                String boneName = animator.getString("name");

                List<Map.Entry<Double, JsonObject>> rotation = new ArrayList<>();
                List<Map.Entry<Double, JsonObject>> position = new ArrayList<>();
                List<Map.Entry<Double, JsonObject>> scale = new ArrayList<>();

                JsonArray keyframes = animator.getJsonArray("keyframes");
                if(keyframes == null) continue;

                for (int k = 0; k < keyframes.size(); k++) {
                    JsonObject keyframe = keyframes.getJsonObject(k);
                    String channel = keyframe.getString("channel");

                    double time = keyframe.getJsonNumber("time").doubleValue();

                    String interpolation = keyframe.getString("interpolation");

                    JsonArray dataPoints = keyframe.getJsonArray("data_points");

                    if(version.isHigherOrEqual(BlockBenchVersion.V5) && (channel.equals("position") || channel.equals("rotation"))) {
                        JsonArrayBuilder builder = Json.createArrayBuilder();

                        for (JsonValue p : dataPoints) {
                            JsonObject dataPoint = p.asJsonObject();

                            JsonObjectBuilder newPoint = Json.createObjectBuilder(dataPoint)
                                    .add("x", MolangInverter.invertMolang(dataPoint.get("x")));

                            if(channel.equals("rotation")) {
                                newPoint.add("y", MolangInverter.invertMolang(dataPoint.get("y")));
                            }

                            builder.add(newPoint.build());
                        }

                        dataPoints = builder.build();
                    }


                    JsonObject built = Json.createObjectBuilder()
                            .add("post", dataPoints)
                            .add("lerp_mode", interpolation)
                            .build();


                    switch (channel) {
                        case "rotation" -> rotation.add(Map.entry(time, built));
                        case "position" -> position.add(Map.entry((time), built));
                        case "scale" -> scale.add(Map.entry((time), built));
                    }
                }

                rotation.sort(Map.Entry.comparingByKey());
                position.sort(Map.Entry.comparingByKey());
                scale.sort(Map.Entry.comparingByKey());

                JsonObjectBuilder rotationJson = Json.createObjectBuilder();
                JsonObjectBuilder positionJson = Json.createObjectBuilder();
                JsonObjectBuilder scaleJson = Json.createObjectBuilder();

                boolean hasRotation = false;
                for (var rotation_ : rotation) {
                    hasRotation = true;
                    rotationJson.add(rotation_.getKey().toString(), rotation_.getValue());
                }

                boolean hasPosition = false;
                for (var position_ : position) {
                    hasPosition = true;
                    positionJson.add(position_.getKey().toString(), position_.getValue());
                }

                boolean hasScale = false;
                for (var scale_ : scale) {
                    hasScale = true;
                    scaleJson.add(scale_.getKey().toString(), scale_.getValue());
                }

                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                if(hasRotation) {
                    objectBuilder.add("rotation", rotationJson);
                }
                if(hasPosition) {
                    objectBuilder.add("position", positionJson);
                }
                if(hasScale) {
                    objectBuilder.add("scale", scaleJson);
                }
                bones.add(boneName, objectBuilder.build());
            }

            JsonObject built = Json.createObjectBuilder()
                    .add("loop", animation.getString("loop").equals("loop"))
                    .add("override_previous_animation", animation.getBoolean("override"))
                    .add("animation_length", length)
                    .add("bones", bones)
                    .build();

            animations.add(name, built);
        }

        return animations.build();
    }
}
