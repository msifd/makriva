package msifeed.makriva.storage;

import msifeed.makriva.MakrivaShared;
import msifeed.makriva.encoding.ShapeCodec;
import msifeed.makriva.model.Shape;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ShapeStorage {
    private static final Marker STORAGE = MarkerManager.getMarker("Storage");

    private final Map<String, Shape> shapes = new HashMap<>();
    private final IStorageBridge bridge;

    private String currentShapeName = "initial non-null value";

    public ShapeStorage(IStorageBridge bridge, String lastShapeName) {
        this.shapes.put(Shape.DEFAULT.name, Shape.DEFAULT);
        this.bridge = bridge;

        final Path dir = Paths.get(MakrivaShared.MOD_ID);

        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            MakrivaShared.LOG.error(STORAGE, "Failed to create Makriva dir", e);
            return;
        }

        try {
            Files.walk(dir)
                    .filter(file -> file.toString().endsWith(".json"))
                    .forEach(this::loadShapeFile);
        } catch (IOException e) {
            MakrivaShared.LOG.error(STORAGE, "Failed to walk through existing shapes", e);
        }

        this.currentShapeName = null;
        selectCurrentShape(lastShapeName);

        try {
            new Thread(new StorageWatcher(dir, this)).start();
        } catch (IOException e) {
            MakrivaShared.LOG.error(STORAGE, "Failed to setup watcher on Makriva dir", e);
        }
    }

    public Map<String, Shape> getShapes() {
        return shapes;
    }

    @Nonnull
    public Shape getCurrentShape() {
        return shapes.getOrDefault(currentShapeName, Shape.DEFAULT);
    }

    public boolean isCurrentShape(@Nullable String name) {
        return name != null && name.equals(currentShapeName);
    }

    public boolean isKnownShape(@Nullable String name) {
        return name != null && shapes.containsKey(name);
    }

    public boolean isKnownShape(String name, Shape shape) {
        final Shape local = shapes.get(name);
        return local != null && local.equals(shape);
    }

    public void selectCurrentShape(@Nullable String name) {
        if (name != null && isCurrentShape(null)) return;

        if (isKnownShape(name)) {
            MakrivaShared.LOG.info("Select current shape: " + name);
            currentShapeName = name;
            bridge.uploadShape(getCurrentShape());
            bridge.currentShapeChanged(name);
        } else if (currentShapeName != null) {
            bridge.currentShapeChanged(null);
        }
    }

    public void addShape(String name, Shape shape) {
        if (shape == null || isKnownShape(name, shape)) return;

        MakrivaShared.LOG.info("Update shape: " + name + ":" + shape.checksum);
        shape.name = name;
        shapes.put(name, shape);

        if (currentShapeName == null) {
            selectCurrentShape(name);
        }
        if (isCurrentShape(name)) {
            bridge.uploadShape(shape);
        }
    }

    public void removeShape(String filename) {
        final String name = filename.replace(".json", "");

        MakrivaShared.LOG.info("Remove shape: " + name);
        shapes.remove(name);

        if (isCurrentShape(name)) {
            selectCurrentShape(null);
        }
    }

    public void loadShapeFile(Path filePath) {
        final String filename = filePath.getFileName().toString();

        final byte[] bytes;
        try {
            bytes = Files.readAllBytes(filePath);
            if (bytes.length == 0) return;
        } catch (IOException e) {
            MakrivaShared.LOG.error(STORAGE, "Failed to read shape {}. Error: {}", filename, e);
            return;
        }

        final String name = filename.replace(".json", "");
        if (!isValidName(name)) return;

        final long checksum = ShapeCodec.checksum(bytes);
        if (shapes.getOrDefault(name, Shape.DEFAULT).checksum == checksum) return;

        final Shape shape;
        try {
            shape = ShapeCodec.fromBytes(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            MakrivaShared.LOG.warn(STORAGE, "Failed to parse shape {}. Error: {}", filePath.getFileName(), e);
            bridge.displayError("Failed to parse shape " + filePath.getFileName() + ". Error: " + e.getMessage());
            return;
        }

        addShape(name, shape);
    }

    public boolean isValidName(String name) {
        return !name.startsWith("<");
    }
}
