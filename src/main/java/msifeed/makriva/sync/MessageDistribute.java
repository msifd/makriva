package msifeed.makriva.sync;

import io.netty.buffer.ByteBuf;
import msifeed.makriva.data.Shape;
import msifeed.makriva.sync.SyncRelay.CheckedBytes;
import msifeed.makriva.utils.ShapeCodec;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageDistribute implements IMessage {
    public Map<UUID, CheckedBytes> shapeBytes;
    public Map<UUID, Shape> shapes;

    public MessageDistribute() {
    }

    public MessageDistribute(Map<UUID, CheckedBytes> shapeBytes) {
        this.shapeBytes = shapeBytes;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        final int len = buf.readInt();
        shapes = new HashMap<>(len);
        for (int i = 0; i < len; i++) {
            final UUID uuid = new UUID(buf.readLong(), buf.readLong());
            final Shape shape = ShapeCodec.readShape(buf);
            shapes.put(uuid, shape);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(shapeBytes.size());
        for (Map.Entry<UUID, CheckedBytes> e : shapeBytes.entrySet()) {
            buf.writeLong(e.getKey().getMostSignificantBits());
            buf.writeLong(e.getKey().getLeastSignificantBits());

            final byte[] bytes = e.getValue().bytes;
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        }
    }
}