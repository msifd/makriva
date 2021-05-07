package msifeed.makriva.storage;

import com.google.gson.JsonParseException;
import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import msifeed.makriva.sync.SyncRelay;
import msifeed.makriva.utils.ShapeCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ShapeStorage {

    private final Map<String, Shape> shapes = new HashMap<>();
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
        } catch (IOException e) {
            Makriva.LOG.error("Failed to walk through existing shapes", e);
        }

        try {
            new Thread(new StorageWatcher(dir, this)).start();
        } catch (IOException e) {
            Makriva.LOG.error("Failed to setup watcher on Makriva dir", e);
        }
    }

    public Path getShapeFile(String name) {
        return Paths.get(Makriva.MOD_ID).resolve(name + ".json");
    }

    public Map<String, Shape> getShapes() {
        return shapes;
    }

    @Nonnull
    public Shape getCurrentShape() {
        return shapes.getOrDefault(currentShape, Shape.DEFAULT);
    }

    public void setCurrentShape(String name) {
        if (shapes.containsKey(name) && !currentShape.equals(name)) {
            Makriva.LOG.info("Select current shape: " + name);
            currentShape = name;
            SyncRelay.upload(getCurrentShape());
        }
    }

    public boolean isKnownShape(String name, Shape shape) {
        final Shape local = shapes.get(name);
        if (local == null) return false;
        return local.equals(shape);
    }

    public void addShape(String filename, Shape shape) {
        if (shape == null || isKnownShape(filename, shape)) return;

        shape.name = filename;

        Makriva.LOG.info("Update shape: " + shape.name + ":" + shape.checksum);
        shapes.put(filename, shape);

        if (currentShape.isEmpty()) {
            findCurrentShape();
        }
        if (currentShape.equals(filename)) {
            SyncRelay.upload(getCurrentShape());
        }
    }

    public void removeShape(String filename) {
        Makriva.LOG.info("Remove shape: " + filename);
        shapes.remove(filename);

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
            final String name = filename.replace(".json", "");
            final long checksum = ShapeCodec.checksum(bytes);
            if (shapes.getOrDefault(name, Shape.DEFAULT).checksum == checksum) return;

            addShape(name, ShapeCodec.fromBytes(bytes));
        } catch (JsonParseException e) {
            Makriva.LOG.warn("Failed to parse shape {}. Error: {}", filePath.getFileName(), e);
            tryLogToPlayer("Failed to parse shape " + filePath.getFileName() + ". Error: " + e.getMessage());
        }
    }

    private void findCurrentShape() {
        setCurrentShape(shapes.keySet().stream()
                .findFirst()
                .orElse(""));
    }

    private void tryLogToPlayer(String msg) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;

        final ITextComponent comp = new TextComponentString(msg);
        comp.getStyle().setColor(TextFormatting.RED);
        player.sendMessage(comp);
    }
}
