package msifeed.makriva.sync.packet;

import io.netty.buffer.ByteBuf;
import msifeed.makriva.data.Shape;
import msifeed.makriva.sync.ShapeSync;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class UploadMessage implements IMessage, IMessageHandler<UploadMessage, IMessage> {
    private Shape shape;

    public UploadMessage() {
    }

    public UploadMessage(Shape shape) {
        this.shape = shape;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        shape = ShapeCodec.readShapeSpec(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ShapeCodec.writeShapeSpec(buf, shape);
    }

    /**
     * Server side handler
     */
    @Override
    public IMessage onMessage(UploadMessage message, MessageContext ctx) {
        if (message.shape == null) return null;

        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
            final UUID uuid = ctx.getServerHandler().player.getGameProfile().getId();
            ShapeSync.getShapes().put(uuid, message.shape);
            ShapeSync.broadcastShape(uuid, message.shape);
        });

        return null;
    }
}
