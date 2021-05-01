package msifeed.makriva.sync.packet;

import io.netty.buffer.ByteBuf;
import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import msifeed.makriva.sync.ShapeSync;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DistributeMessage implements IMessage, IMessageHandler<DistributeMessage, IMessage> {
    private Map<UUID, Shape> shapes;

    public DistributeMessage() {
    }

    public DistributeMessage(Map<UUID, Shape> shapes) {
        this.shapes = shapes;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        final int len = buf.readInt();
        shapes = new HashMap<>(len);
        for (int i = 0; i < len; i++) {
            final UUID uuid = new UUID(buf.readLong(), buf.readLong());
            shapes.put(uuid, ShapeCodec.readShapeSpec(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(shapes.size());
        for (Map.Entry<UUID, Shape> e : shapes.entrySet()) {
            buf.writeLong(e.getKey().getMostSignificantBits());
            buf.writeLong(e.getKey().getLeastSignificantBits());
            ShapeCodec.writeShapeSpec(buf, e.getValue());
        }
    }

    /**
     * Client side handler
     */
    @Override
    public IMessage onMessage(DistributeMessage message, MessageContext ctx) {
        if (message.shapes == null) return null;
        Makriva.LOG.info("Received " + message.shapes.size() + " shapes");

        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
            ShapeSync.INSTANCE.updateShapes(message.shapes);
        });

        return null;
    }
}
