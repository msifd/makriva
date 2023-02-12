package msifeed.makriva.sync;

import io.netty.buffer.ByteBuf;
import msifeed.makriva.model.Shape;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageUpload implements IMessage, IMessageHandler<MessageUpload, IMessage> {
    private final PayloadUpload payload;

    public MessageUpload() {
        this.payload = new PayloadUpload();
    }

    public MessageUpload(Shape shape) {
        this.payload = new PayloadUpload(shape);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        payload.decodeFrom(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        payload.encodeInto(buf);
    }

    @Override
    public IMessage onMessage(MessageUpload message, MessageContext ctx) {
        if (message.payload.compressed == null || message.payload.compressed.length == 0) return null;

        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
            final UUID uuid = ctx.getServerHandler().player.getGameProfile().getId();
            PayloadUpload.serverHandle(message.payload, uuid);
        });

        return null;
    }
}
