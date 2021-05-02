package msifeed.makriva.storage;

import com.google.gson.JsonParseException;
import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import msifeed.makriva.utils.ShapeCodec;
import msifeed.makriva.utils.ShapeRegistry;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ShapeStorage extends ShapeRegistry<String> {
    private String currentShape = "";

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
                    .forEach(this::loadShapeFile);
            findCurrentShape();
        } catch (IOException e) {
            Makriva.LOG.error("Failed to walk through existing shapes", e);
        }

        try {
            new Thread(new StorageWatcher(dir, this)).start();
        } catch (IOException e) {
            Makriva.LOG.error("Failed to setup watcher on Makriva dir", e);
        }
    }

    @Nullable
    public Shape getCurrentShape() {
        return shapes.get(currentShape);
    }

    @Override
    public void addShape(String filename, Shape shape) {
        if (shape == null || isKnownShape(filename, shape)) return;

        Makriva.LOG.info("Update shape: " + filename + ":" + shape.checksum);
        super.addShape(filename, shape);

        if (currentShape.equals(filename)) {
            Makriva.SYNC.uploadShape(getCurrentShape());
        }
    }

    @Override
    public void removeShape(String filename) {
        Makriva.LOG.info("Remove shape: " + filename);
        super.removeShape(filename);

        if (currentShape.equals(filename)) {
            findCurrentShape();
        }
    }

    public void loadShapeFile(Path filePath) {
        final String filename = filePath.getFileName().toString();

        final byte[] bytes;
        try {
            bytes = Files.readAllBytes(filePath);
            if (bytes.length == 0) return;
        } catch (IOException e) {
            Makriva.LOG.error("Failed to read shape {}. Error: {}", filename, e);
            return;
        }

        try {
            addShape(filename, ShapeCodec.fromBytes(bytes));
        } catch (JsonParseException e) {
            Makriva.LOG.warn("Failed to parse shape {}. Error: {}", filePath.getFileName(), e);
        }
    }

    private void findCurrentShape() {
        if (!currentShape.isEmpty()) return;

        currentShape = shapes.keySet().stream()
                .findFirst()
                .orElse("");
    }
}
