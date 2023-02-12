package msifeed.makriva.sync;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import msifeed.makriva.storage.CheckedBytes;

import java.util.Map;
import java.util.UUID;

public class MessageDistribute implements IMessage, IMessageHandler<MessageDistribute, IMessage> {
    private final PayloadDistribute payload;

    public MessageDistribute() {
        payload = new PayloadDistribute();
    }

    public MessageDistribute(Map<UUID, CheckedBytes> shapeBytes) {
        payload = new PayloadDistribute(shapeBytes);
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
    public IMessage onMessage(MessageDistribute message, MessageContext ctx) {
        if (message.payload.shapes == null) return null;

        PayloadDistribute.clientHandle(message.payload);
        return null;
    }
}
