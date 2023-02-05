package msifeed.makriva.render;

import msifeed.makriva.model.Shape;

import java.util.UUID;

public interface IRenderBridge<Model extends IShapeModel> {
    UUID getPlayerUuid();

    Model buildModel(Shape shape);

    void updatePlayerEval();

    void invalidateAllSkins();

    void invalidateSkin(UUID uuid);
}
