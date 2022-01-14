package msifeed.makriva.storage;

import com.google.gson.Gson;
import msifeed.makriva.encoding.ShapeCodec;
import msifeed.makriva.model.SharedShape;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractShapeRelay {
    protected final Map<UUID, CheckedBytes> encodedShapes = new HashMap<>();
    protected final Gson gson = new Gson();

    protected abstract void relayAll(Map<UUID, CheckedBytes> toRelay);
    protected abstract void updateSharedData(UUID uuid, SharedShape shared);

    protected void maybeAddShape(UUID uuid, byte[] shapeBytes) {
        final long checksum = ShapeCodec.checksum(shapeBytes);
        if (!isKnownShape(uuid, checksum)) {
            final CheckedBytes checked = new CheckedBytes(shapeBytes, checksum);
            encodedShapes.put(uuid, checked);
            updateSharedShape(uuid, shapeBytes);
            relayAll(Collections.singletonMap(uuid, checked));
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
            updateSharedData(uuid, gson.fromJson(str, SharedShape.class));
        } catch (Exception ignored) {
        }
    }
}
