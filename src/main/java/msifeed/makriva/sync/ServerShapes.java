package msifeed.makriva.sync;

import msifeed.makriva.Makriva;
import msifeed.makriva.MakrivaConfig;
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
        Makriva.LOG.info("Update shared shape {}", uuid);

        shape.eyeHeight.replaceAll((p, h) -> Math.min(h, MakrivaConfig.server.maxEyeHeight));
        shape.boundingBox.forEach((p, s) -> s[1] = Math.max(s[1], MakrivaConfig.server.minBBHeight));

        shapes.put(uuid, shape);
    }
}
