package net.worldseed.resourcepack;

import net.worldseed.resourcepack.multipart.generator.ModelGenerator;
import net.worldseed.resourcepack.multipart.generator.TextureGenerator;
import net.worldseed.resourcepack.multipart.parser.ModelParser;

import javax.json.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PackBuilder {

    public static JsonArray applyInflate(JsonArray from, double inflate) {
        JsonArrayBuilder inflated = Json.createArrayBuilder();
        for (int i = 0; i < from.size(); ++i) {
            double val = from.getJsonNumber(i).doubleValue() + inflate;
            inflated.add(val);
        }
        return inflated.build();
    }

    public static ConfigJson generate(Path bbmodel, Path resourcepack, Path modelDataPath, String namespace) throws Exception {
        Files.createDirectories(resourcepack);
        Files.createDirectories(modelDataPath);

        Map<Path, JsonObject> additionalStateFiles = new HashMap<>();

        List<Model> entityModels = recursiveFileSearch(bbmodel, bbmodel, additionalStateFiles);

        Path assets = resourcepack.resolve("assets/" + namespace + "/");
        Path texturePathMobs = assets.resolve("textures/mobs/");
        Path modelPathMobs = assets.resolve("models/mobs/");
        Path baseModelPath = assets.resolve("items/");

        Files.createDirectories(texturePathMobs);
        Files.createDirectories(modelPathMobs);
        Files.createDirectories(baseModelPath);

        JsonObject modelMappings = writeCustomModels(entityModels, modelDataPath, texturePathMobs, modelPathMobs, baseModelPath);

        return new ConfigJson(modelMappings.toString());
    }

    private static List<Model> recursiveFileSearch(Path rootPath, Path path, Map<Path, JsonObject> additionalStateFiles) {
        try {
            try (var fileStream = Files.list(path)) {
                var fileList = fileStream.toList();

                var files = fileList.stream()
                        .filter(Files::isRegularFile)
                        .filter(file -> file.getFileName().toString().endsWith(".bbmodel"))
                        .map(entityModel -> {
                            try {
                                Path pathName = rootPath.relativize(entityModel);
                                Path stateFile = path.resolve(entityModel.getFileName() + ".states");

                                if (Files.exists(stateFile)) {
                                    JsonObject m = Json.createReader(Files.newInputStream(stateFile)).readObject();
                                    additionalStateFiles.put(pathName, m);
                                }

                                String modelName = pathName.toString().replace("\\", "/");
                                return new Model(Files.readString(entityModel, StandardCharsets.UTF_8), modelName, additionalStateFiles.get(pathName));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }).toList();

                var dirs = fileList.stream()
                    .filter(Files::isDirectory)
                    .map(dir -> recursiveFileSearch(rootPath, dir, additionalStateFiles))
                    .flatMap(List::stream)
                    .toList();

                return Stream.of(files, dirs).flatMap(List::stream).toList();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static JsonObject writeCustomModels(List<Model> entityModels, Path modelDataPath, Path texturePathMobs, Path modelPathMobs, Path itemModelPath) throws Exception {
        Map<String, ModelGenerator.BBEntityModel> res = new HashMap<>();

        for (Model entityModel : entityModels) {
            ModelGenerator.BBEntityModel bbModel = ModelGenerator.generate(entityModel);
            var modelDir = modelDataPath.resolve(bbModel.id());
            Files.createDirectories(modelDir);

            Files.writeString(modelDir.resolve("model.animation.json"), bbModel.animations().toString(), Charset.defaultCharset());
            Files.writeString(modelDataPath.resolve(bbModel.id()).resolve("model.geo.json"), bbModel.geo().toString(), Charset.defaultCharset());

            res.put(bbModel.id(), bbModel);
        }

        ModelParser.ModelEngineFiles modelData = ModelParser.parse(res.values(), modelPathMobs);

        modelData.models().forEach(model -> {
            var textureData = res.get(model.id()).textures();

            for (var entry : model.textures().entrySet()) {
                TextureGenerator.TextureData found = textureData.get(entry.getKey());
                Path resolvedPath = texturePathMobs.resolve(model.id()).resolve(model.state().name());

                try {
                    Files.createDirectories(resolvedPath);
                    if (found.mcmeta() != null) {
						Files.writeString(resolvedPath.resolve(entry.getKey() + ".png.mcmeta"), found.mcmeta().toString(), StandardCharsets.UTF_8);
                    }

                    Files.write(resolvedPath.resolve(entry.getKey() + ".png"), entry.getValue());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            var modelStatePath = modelPathMobs.resolve(model.id()).resolve(model.state().name());
            try {
                Files.createDirectories(modelStatePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (var entry : model.bones().entrySet()) {
                try {
                    Files.writeString(modelStatePath.resolve(entry.getKey()), entry.getValue().toString(), Charset.defaultCharset());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        for (ModelParser.ItemModelFile itemModelFile : modelData.binding()) {
            Path path = itemModelPath.resolve("bbmodel").resolve(itemModelFile.id() + ".json");
            Files.createDirectories(path.getParent());
            Files.writeString(path, itemModelFile.binding().toString(), Charset.defaultCharset());
        }
        return modelData.mappings();
    }

    public record Model(String data, String name, JsonObject additionalStates) {
    }

    public record ConfigJson(String modelMappings) {
    }
}
