package msifeed.makriva.sync;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import msifeed.makriva.data.Shape;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageUpload implements IMessage {
    public byte[] shapeBytes;

    public MessageUpload() {
    }

    public MessageUpload(Shape shape) {
        this.shapeBytes = shape.source;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        final int len = buf.readInt();
        shapeBytes = ByteBufUtil.getBytes(buf, buf.readerIndex(), len);
        buf.readerIndex(buf.readerIndex() + len);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(shapeBytes.length);
        buf.writeBytes(shapeBytes);
    }
}
