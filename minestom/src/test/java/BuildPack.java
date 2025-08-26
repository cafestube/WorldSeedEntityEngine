import net.worldseed.resourcepack.PackBuilder;
import org.apache.commons.io.FileUtils;

import java.nio.charset.Charset;
import java.nio.file.Path;

public class BuildPack {
    private static final Path TEST_PATH = Path.of("test-files");
    private static final Path BASE_PATH = Path.of("minestom/src/test/resources");
    private static final Path MODEL_PATH = BASE_PATH.resolve("models");

    public static void main(String[] args) throws Exception {
        FileUtils.copyDirectory(TEST_PATH.resolve("resourcepack_template").toFile(), BASE_PATH.resolve("resourcepack").toFile());
        var config = PackBuilder.generate(TEST_PATH.resolve("bbmodel"), BASE_PATH.resolve("resourcepack"), MODEL_PATH,"worldseed");
        FileUtils.writeStringToFile(BASE_PATH.resolve("model_mappings.json").toFile(), config.modelMappings(), Charset.defaultCharset());
    }
}
