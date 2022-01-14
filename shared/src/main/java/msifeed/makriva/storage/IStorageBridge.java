package msifeed.makriva.storage;

import msifeed.makriva.model.Shape;

import javax.annotation.Nullable;

public interface IStorageBridge {
    void currentShapeChanged(@Nullable String newShapeName);

    void uploadShape(Shape shape);

    void displayError(String message);
}
