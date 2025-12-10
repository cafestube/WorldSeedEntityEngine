package net.worldseed.paper.test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.worldseed.multipart.ModelEngine;
import net.worldseed.multipart.ModelRegistry;
import net.worldseed.multipart.data.ModelProvider;
import net.worldseed.paper.test.profiler.ModelProfiler;
import net.worldseed.resourcepack.PackBuilder;
import org.apache.commons.io.FileUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.zeroturnaround.zip.ZipUtil;

import java.io.*;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class TestPlugin extends JavaPlugin implements Listener {

    private String hash;
    public ModelRegistry registry;

    @Override
    public void onEnable() {

        try {
            FileUtils.deleteDirectory(getDataPath().resolve("resourcepack").toFile());

            Path testFiles = Path.of("..", "..", "..", "test-files").normalize();
            if(!Files.exists(testFiles))
                throw new IOException("Test files directory not found: " + testFiles.toAbsolutePath());

            FileUtils.copyDirectory(testFiles.resolve("resourcepack_template").toFile(), getDataPath().resolve("resourcepack").toFile());
            var config = PackBuilder.generate(testFiles.resolve("bbmodel"), getDataPath().resolve("resourcepack"), getDataPath().resolve("models"), "worldseed");
            FileUtils.writeStringToFile(getDataPath().resolve("model_mappings.json").toFile(), config.modelMappings(), Charset.defaultCharset());

            File zipFile = getDataPath().resolve("resourcepack.zip").toFile();
            ZipUtil.pack(getDataPath().resolve("resourcepack").toFile(), zipFile);

            this.hash = calculateMD5(zipFile);
            startHttpServer(zipFile, hash);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        registry = new ModelRegistry("worldseed", ModelProvider.file(getDataPath().resolve("model_mappings.json"), getDataPath().resolve("models")));

        ModelEngine modelEngine = new ModelEngine(this, registry, null);

        getServer().getPluginManager().registerEvents(this, this);
        registerCommand("spawn", new SpawnCommand(modelEngine));

        new ModelProfiler(this).register();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendResourcePacks(ResourcePackInfo.resourcePackInfo()
                .uri(URI.create("http://127.0.0.1:8080/pack?hash=" + hash))
                .hash(hash)
                .build());
    }


    private static void startHttpServer(File zipFile, String hash) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/pack", new PackHandler(zipFile, hash));
        server.start();
    }

    private static String calculateMD5(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] md5sum = digest.digest();
        BigInteger bigInt = new BigInteger(1, md5sum);
        return String.format("%032x", bigInt);
    }

    static class PackHandler implements HttpHandler {
        private final File zipFile;
        private final String expectedHash;

        public PackHandler(File zipFile, String expectedHash) {
            this.zipFile = zipFile;
            this.expectedHash = expectedHash;
        }

        public void handle(HttpExchange t) throws IOException {
            String requestHash = getHashFromQuery(t.getRequestURI().getQuery());
            if (expectedHash.equals(requestHash)) {
                t.sendResponseHeaders(200, zipFile.length());
                OutputStream os = t.getResponseBody();
                Files.copy(zipFile.toPath(), os);
                os.close();
            } else {
                t.sendResponseHeaders(404, 0); // Invalid hash, return 404
            }
        }

        private String getHashFromQuery(String query) {
            if (query != null && !query.isEmpty()) {
                Map<String, String> queryMap = new HashMap<>();
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    queryMap.put(pair[0], pair[1]);
                }
                return queryMap.get("hash");
            }
            return null;
        }
    }
}
