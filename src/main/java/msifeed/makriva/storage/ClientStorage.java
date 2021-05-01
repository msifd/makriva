package msifeed.makriva.storage;

import com.google.gson.Gson;
import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import msifeed.makriva.sync.ShapeSync;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public enum ClientStorage {
    INSTANCE;

    private final Gson gson = new Gson();
    private final Map<String, Shape> shapes = new HashMap<>();

    private String currentShape = "";

    @Nullable
    public static Shape getCurrentShape() {
        return INSTANCE.shapes.get(INSTANCE.currentShape);
    }

    public void init() {
        final Path dir = Paths.get(Makriva.MOD_ID);

        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            Makriva.LOG.error("Failed to create Makriva dir", e);
            return;
        }

        try {
            Files.walk(dir)
                    .filter(file -> file.toString().endsWith(".json"))
                    .forEach(this::updateShapeFile);
            currentShape = shapes.keySet().stream().findFirst().orElse("");
        } catch (IOException e) {
            Makriva.LOG.error("Failed to walk through existing shapes", e);
        }

        try {
            new Thread(new StorageWatcher(dir)).start();
        } catch (IOException e) {
            Makriva.LOG.error("Failed to setup watcher on Makriva dir", e);
        }
    }

    public void updateShapeFile(Path file) {
        final String filename = file.getFileName().toString();
        Makriva.LOG.info("Update shape: " + filename);

        try {
            final Shape shape = parseShape(file);
            if (shape != null)
                shapes.put(filename, shape);
        } catch (IOException e) {
            Makriva.LOG.error("Failed to read shape " + filename, e);
        }

        if (currentShape.equals(filename)) {
            ShapeSync.uploadCurrentShape();
        }
    }

    @Nullable
    private Shape parseShape(Path file) throws IOException {
        final String json = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);

        try {
            return gson.fromJson(json, Shape.class);
        } catch (Exception e) {
            Makriva.LOG.warn("Failed to parse shape " + file.getFileName(), e);
            return null;
        }
    }

    public void removeShapeFile(String filename) {
        Makriva.LOG.info("Remove shape: " + filename);
        shapes.remove(filename);
    }
}
