package msifeed.makriva.sync;

import msifeed.makriva.data.SharedShape;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerShapes {
    private final Map<UUID, SharedShape> shapes = new HashMap<>();

    @Nonnull
    public SharedShape getShape(UUID uuid) {
        return shapes.getOrDefault(uuid, SharedShape.DEFAULT_SHARED);
    }

    public void updateShape(UUID uuid, SharedShape shape) {
        shapes.put(uuid, shape);
    }
}
