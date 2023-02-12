package msifeed.makriva.sync;

import com.google.gson.JsonParseException;
import io.netty.buffer.ByteBuf;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.encoding.ShapeCodec;
import msifeed.makriva.model.Shape;
import msifeed.makriva.storage.CheckedBytes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PayloadDistribute {
    public Map<UUID, CheckedBytes> shapeBytes;
    public Map<UUID, Shape> shapes;

    public PayloadDistribute() {
    }

    public PayloadDistribute(Map<UUID, CheckedBytes> shapeBytes) {
        this.shapeBytes = shapeBytes;
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
