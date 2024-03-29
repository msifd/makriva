package msifeed.makriva.sync;

import com.google.gson.JsonParseException;
import io.netty.buffer.ByteBuf;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.encoding.ShapeCodec;
import msifeed.makriva.model.Shape;
import msifeed.makriva.storage.CheckedBytes;

import java.io.IOException;
import java.util.*;

public class PayloadDistribute {
    public static final long DIST_MAX_CHUNK_SIZE = 1024 * 1024;

    public Map<UUID, CheckedBytes> shapeBytes;
    public Map<UUID, Shape> shapes;

    public PayloadDistribute() {
    }

    public PayloadDistribute(Map<UUID, CheckedBytes> shapeBytes) {
        this.shapeBytes = shapeBytes;
    }

    public static List<Map<UUID, CheckedBytes>> splitIntoChunks(Map<UUID, CheckedBytes> allShapes) {
        final List<Map<UUID, CheckedBytes>> chunks = new ArrayList<>();
        Map<UUID, CheckedBytes> currentChunk = new HashMap<>();
        long chunkSize = 0;

        for (Map.Entry<UUID, CheckedBytes> e : allShapes.entrySet()) {
            currentChunk.put(e.getKey(), e.getValue());
            chunkSize += e.getValue().compressed.length;
            if (chunkSize > DIST_MAX_CHUNK_SIZE) {
                chunks.add(currentChunk);
                currentChunk = new HashMap<>();
                chunkSize = 0;
            }
        }
        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk);
        }

        return chunks;
    }

    public static void clientHandle(PayloadDistribute message) {
        MakrivaShared.LOG.info("Received " + message.shapes.size() + " shapes");

        message.shapes.forEach(MakrivaShared.MODELS::updateShape);
        message.shapes.forEach(MakrivaShared.SHARED::update);
    }

    public void decodeFrom(ByteBuf buf) {
        final int len = buf.readInt();
        shapes = new HashMap<>(len);
        for (int i = 0; i < len; i++) {
            final UUID uuid = new UUID(buf.readLong(), buf.readLong());

            final int shapeLen = buf.readInt();
            final byte[] compressed = new byte[shapeLen];
            buf.readBytes(compressed);

            try {
                shapes.put(uuid, ShapeCodec.fromCompressed(compressed));
            } catch (IOException | JsonParseException e) {
                final long checksum = ShapeCodec.checksum(compressed);
                MakrivaShared.LOG.warn("Failed to decode remote shape: " + checksum);
                e.printStackTrace();
            }
        }
    }

    public void encodeInto(ByteBuf buf) {
        buf.writeInt(shapeBytes.size());
        for (Map.Entry<UUID, CheckedBytes> e : shapeBytes.entrySet()) {
            buf.writeLong(e.getKey().getMostSignificantBits());
            buf.writeLong(e.getKey().getLeastSignificantBits());

            final byte[] compressed = e.getValue().compressed;
            buf.writeInt(compressed.length);
            buf.writeBytes(compressed);
        }
    }
}
