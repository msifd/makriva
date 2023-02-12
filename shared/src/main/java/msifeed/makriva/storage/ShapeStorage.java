package msifeed.makriva.storage;

import msifeed.makriva.MakrivaShared;
import msifeed.makriva.encoding.ShapeCodec;
import msifeed.makriva.model.Shape;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShapeStorage {
    private static final Marker STORAGE = MarkerManager.getMarker("Storage");

    private final Map<String, Shape> shapes = new HashMap<>();
    private final IStorageBridge bridge;

    private String currentShapeName = "initial non-null value";

    public ShapeStorage(IStorageBridge bridge) {
        this.shapes.put(Shape.DEFAULT.name, Shape.DEFAULT);
        this.bridge = bridge;

        final Path dir = Paths.get(MakrivaShared.MOD_ID);

        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            MakrivaShared.LOG.error(STORAGE, "Failed to create Makriva dir", e);
            return;
        }

        try (Stream<Path> files = Files.walk(dir)) {
            files.filter(file -> file.toString().endsWith(".json"))
                    .forEach(this::loadShapeFile);
        } catch (IOException e) {
            MakrivaShared.LOG.error(STORAGE, "Failed to walk through existing shapes", e);
        }

        this.currentShapeName = null;
        selectCurrentShape(MakrivaShared.CFG.get().shape);

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

            MakrivaShared.RELAY.upload(getCurrentShape());
            MakrivaShared.CFG.selectShape(name);
        } else if (currentShapeName != null) {
            MakrivaShared.CFG.selectShape(Shape.DEFAULT.name);
        }
    }

    public void addShape(String name, Shape shape) {
        if (shape == null || isKnownShape(name, shape)) return;

        MakrivaShared.LOG.info("Update shape: " + name + ":" + shape.checksum + " size: " + shape.compressed.length);
        shape.name = name;
        shapes.put(name, shape);

        if (currentShapeName == null) {
            selectCurrentShape(name);
        }
        if (isCurrentShape(name)) {
            MakrivaShared.RELAY.upload(shape);
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
        final String name = filename.replace(".json", "");
        if (!isValidName(name)) return;

        final String source;
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            source = reader.lines().collect(Collectors.joining());
        } catch (IOException e) {
            MakrivaShared.LOG.error(STORAGE, "Failed to read shape {}. Error: {}", filename, e);
            return;
        }

        try {
            final Shape shape = ShapeCodec.fromString(source);

            // Ignore if shape is the same
            if (shapes.getOrDefault(name, Shape.DEFAULT).checksum == shape.checksum) return;

            addShape(name, shape);
        } catch (Exception e) {
            MakrivaShared.LOG.warn(STORAGE, "Failed to parse shape {}. Error: {}", filePath.getFileName(), e);
            bridge.displayError("Failed to parse shape " + filePath.getFileName() + ". Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isValidName(String name) {
        return !name.startsWith("<");
    }
}
