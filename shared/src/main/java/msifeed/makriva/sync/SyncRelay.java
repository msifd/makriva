package msifeed.makriva.sync;

import com.google.gson.Gson;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.config.ConfigData;
import msifeed.makriva.encoding.ShapeCodec;
import msifeed.makriva.model.Shape;
import msifeed.makriva.model.SharedShape;
import msifeed.makriva.storage.CheckedBytes;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SyncRelay {
    protected final Map<UUID, CheckedBytes> encodedShapes = new HashMap<>();
    protected final Map<UUID, SharedShape> sharedShapes = new HashMap<>();
    protected final Gson gson = new Gson();

    protected final INetworkBridge bridge;

    public SyncRelay(INetworkBridge bridge) {
        this.bridge = bridge;
    }

    @Nonnull
    public SharedShape get(UUID uuid) {
        return sharedShapes.getOrDefault(uuid, SharedShape.DEFAULT_SHARED);
    }

    public Map<UUID, CheckedBytes> getEncodedShapes() {
        return encodedShapes;
    }

    public void upload(Shape shape) {
        bridge.upload(shape);
    }

    public void update(UUID uuid, SharedShape shape) {
        MakrivaShared.LOG.info("Update shared shape {}", uuid);

        final ConfigData cfg = MakrivaShared.CFG.get();
        shape.eyeHeight.replaceAll((p, h) -> Math.min(h, cfg.maxEyeHeight));
        shape.boundingBox.forEach((p, s) -> s[1] = Math.max(s[1], cfg.minBBHeight));

        sharedShapes.put(uuid, shape);
    }

    public void maybeAddShape(UUID uuid, byte[] shapeBytes) {
        final long checksum = ShapeCodec.checksum(shapeBytes);
        if (!isKnownShape(uuid, checksum)) {
            final CheckedBytes checked = new CheckedBytes(shapeBytes, checksum);
            encodedShapes.put(uuid, checked);
            updateSharedShape(uuid, shapeBytes);
            bridge.relayToAll(Collections.singletonMap(uuid, checked));
        }
    }

    protected boolean isKnownShape(UUID uuid, long checksum) {
        final CheckedBytes local = encodedShapes.get(uuid);
        if (local == null) return false;
        return local.checksum == checksum;
    }

    protected void updateSharedShape(UUID uuid, byte[] shapeBytes) {
        final String str = new String(shapeBytes, StandardCharsets.UTF_8);
        try {
            MakrivaShared.SHARED.update(uuid, gson.fromJson(str, SharedShape.class));
        } catch (Exception ignored) {
        }
    }
}
