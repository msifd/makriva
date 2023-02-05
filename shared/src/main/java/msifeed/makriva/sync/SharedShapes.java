package msifeed.makriva.sync;

import msifeed.makriva.MakrivaShared;
import msifeed.makriva.config.ConfigData;
import msifeed.makriva.model.SharedShape;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SharedShapes {
    private final Map<UUID, SharedShape> shapes = new HashMap<>();

    @Nonnull
    public SharedShape get(UUID uuid) {
        return shapes.getOrDefault(uuid, SharedShape.DEFAULT_SHARED);
    }

    public void update(UUID uuid, SharedShape shape) {
        MakrivaShared.LOG.info("Update shared shape {}", uuid);

        final ConfigData cfg = MakrivaShared.CFG.get();
        shape.eyeHeight.replaceAll((p, h) -> Math.min(h, cfg.maxEyeHeight));
        shape.boundingBox.forEach((p, s) -> s[1] = Math.max(s[1], cfg.minBBHeight));

        shapes.put(uuid, shape);
    }
}
