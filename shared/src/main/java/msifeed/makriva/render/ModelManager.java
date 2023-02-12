package msifeed.makriva.render;

import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.Shape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModelManager<Model extends IShapeModel> {
    protected final Map<UUID, Shape> shapes = new HashMap<>();
    protected final Map<UUID, Model> models = new HashMap<>();
    private final IRenderBridge<Model> bridge;
    protected Model previewModel = null;

    public ModelManager(IRenderBridge<Model> bridge) {
        this.bridge = bridge;
    }

    public boolean hasShape(UUID uuid) {
        return shapes.containsKey(uuid);
    }

    @Nonnull
    public Shape getShape(UUID uuid) {
        if (previewModel != null) {
            if (bridge.getPlayerUuid().equals(uuid)) return previewModel.getShape();
        }
        return shapes.getOrDefault(uuid, Shape.DEFAULT);
    }

    @Nonnull
    public Model getModel(UUID uuid) {
        if (previewModel != null) {
            if (bridge.getPlayerUuid().equals(uuid)) return previewModel;
        }
        return models.computeIfAbsent(uuid, id -> bridge.buildModel(getShape(uuid)));
    }

    @Nullable
    public Model getModelWithoutBuild(UUID uuid) {
        if (previewModel != null) {
            if (bridge.getPlayerUuid().equals(uuid)) return previewModel;
        }
        return models.get(uuid);
    }

    @Nullable
    public Model getPreviewModel() {
        return previewModel;
    }

    public void updateShape(UUID uuid, Shape shape) {
        bridge.invalidateSkin(uuid);
        models.remove(uuid);
        shapes.put(uuid, shape);
    }

    public void invalidateSkin(UUID uuid) {
        bridge.invalidateSkin(uuid);
    }

    public void invalidateAllSkins() {
        bridge.invalidateAllSkins();
    }

    public void selectPreview(String name) {
        final Shape shape = MakrivaShared.STORAGE.getShapes().get(name);
        if (shape == null) {
            MakrivaShared.LOG.warn("Can't preview unknown shape: " + name);
            return;
        }

        MakrivaShared.LOG.debug("Build shape preview model checksum: " + shape.checksum);
        previewModel = bridge.buildModel(shape);
        invalidateSkin(bridge.getPlayerUuid());

        bridge.updatePlayerEval();
    }

    public void clearPreview() {
        previewModel = null;
        invalidateSkin(bridge.getPlayerUuid());
    }

    public void discardModels() {
        MakrivaShared.LOG.info("Discard all shape models");
        models.clear();
        previewModel = null;
    }
}
