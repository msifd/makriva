package msifeed.makriva.utils;

import msifeed.makriva.data.Shape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class ShapeRegistry<K> {
    protected final Map<K, Shape> shapes = new HashMap<>();

    public boolean isKnownShape(K key, Shape shape) {
        final Shape local = shapes.get(key);
        if (local == null) return false;
        return local.equals(shape);
    }

    @Nullable
    public Shape get(K key) {
        return shapes.get(key);
    }

    public void addShape(K key, Shape shape) {
        shapes.put(key, shape);
    }

    public void removeShape(K key) {
        shapes.remove(key);
    }
}
