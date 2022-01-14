package msifeed.makriva.sync;

import io.netty.buffer.ByteBuf;
import msifeed.makriva.Makriva;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.encoding.ShapeCodec;
import msifeed.makriva.model.Shape;
import msifeed.makriva.storage.CheckedBytes;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageDistribute implements IMessage, IMessageHandler<MessageDistribute, IMessage> {
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

    @Override
    public IMessage onMessage(MessageDistribute message, MessageContext ctx) {
        if (message.shapes == null) return null;
        MakrivaShared.LOG.info("Received " + message.shapes.size() + " shapes");

        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
            message.shapes.forEach(Makriva.MODELS::updateShape);
            message.shapes.forEach(Makriva.SHARED::update);
        });

        return null;
    }
}
