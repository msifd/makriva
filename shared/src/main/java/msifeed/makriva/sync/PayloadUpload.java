package msifeed.makriva.sync;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.Shape;

import java.util.UUID;

public class PayloadUpload {
    public byte[] shapeBytes;

    public PayloadUpload() {
    }

    public PayloadUpload(Shape shape) {
        this.shapeBytes = shape.source;
    }

    public static void serverHandle(PayloadUpload message, UUID sender) {
        MakrivaShared.RELAY.maybeAddShape(sender, message.shapeBytes);
    }

    public void decodeFrom(ByteBuf buf) {
        final int len = buf.readInt();
        shapeBytes = ByteBufUtil.getBytes(buf, buf.readerIndex(), len);
        buf.readerIndex(buf.readerIndex() + len);
    }

    public void encodeInto(ByteBuf buf) {
        buf.writeInt(shapeBytes.length);
        buf.writeBytes(shapeBytes);
    }
}
