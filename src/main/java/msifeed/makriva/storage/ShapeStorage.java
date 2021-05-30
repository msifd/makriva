package msifeed.makriva.storage;

import com.google.gson.JsonParseException;
import msifeed.makriva.Makriva;
import msifeed.makriva.MakrivaConfig;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ShapeStorage {
    private final Map<String, Shape> shapes = new HashMap<>();

    public ShapeStorage() {
        shapes.put(Shape.DEFAULT.name, Shape.DEFAULT);
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

    public Map<String, Shape> getShapes() {
        return shapes;
    }

    @Nonnull
    public Shape getCurrentShape() {
        return shapes.getOrDefault(MakrivaConfig.client.shape, Shape.DEFAULT);
    }

    public void setCurrentShape(String name) {
        if (shapes.containsKey(name) && !MakrivaConfig.client.shape.equals(name)) {
            Makriva.LOG.info("Select current shape: " + name);
            MakrivaConfig.client.shape = name;
            SyncRelay.upload(getCurrentShape());

            MakrivaConfig.sync();
        }
    }

    public boolean isKnownShape(String name, Shape shape) {
        final Shape local = shapes.get(name);
        if (local == null) return false;
        return local.equals(shape);
    }

    public void addShape(String name, Shape shape) {
        if (shape == null || isKnownShape(name, shape)) return;

        shape.name = name;

        Makriva.LOG.info("Update shape: " + shape.name + ":" + shape.checksum);
        shapes.put(name, shape);

        if (MakrivaConfig.client.shape.isEmpty()) {
            findCurrentShape();
        }
        if (MakrivaConfig.client.shape.equals(name)) {
            SyncRelay.upload(getCurrentShape());
        }
    }

    public void removeShape(String filename) {
        final String name = filename.replace(".json", "");

        Makriva.LOG.info("Remove shape: " + name);
        shapes.remove(name);

        if (MakrivaConfig.client.shape.equals(name)) {
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
            if (!isValidName(name)) return;

            final long checksum = ShapeCodec.checksum(bytes);
            if (shapes.getOrDefault(name, Shape.DEFAULT).checksum == checksum) return;

            addShape(name, ShapeCodec.fromBytes(bytes));
        } catch (JsonParseException e) {
            Makriva.LOG.warn("Failed to parse shape {}. Error: {}", filePath.getFileName(), e);
            tryLogToPlayer("Failed to parse shape " + filePath.getFileName() + ". Error: " + e.getMessage());
        }
    }

    public boolean isValidName(String name) {
        return !name.startsWith("<");
    }

    private void findCurrentShape() {
        setCurrentShape(shapes.keySet().stream()
                .findFirst()
                .orElse(Shape.DEFAULT.name));
    }

    private void tryLogToPlayer(String msg) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;

        final ITextComponent comp = new TextComponentString(msg);
        comp.getStyle().setColor(TextFormatting.RED);
        player.sendMessage(comp);
    }
}
