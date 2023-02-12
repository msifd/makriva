package msifeed.makriva.sync;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import msifeed.makriva.model.Shape;

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

        final UUID uuid = ctx.getServerHandler().playerEntity.getGameProfile().getId();
        PayloadUpload.serverHandle(message.payload, uuid);

        return null;
    }
}
