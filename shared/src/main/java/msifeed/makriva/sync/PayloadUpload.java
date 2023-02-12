package msifeed.makriva.sync;

import io.netty.buffer.ByteBuf;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.Shape;

import java.util.UUID;

public class PayloadUpload {
    public byte[] compressed;

    public PayloadUpload() {
    }

    public PayloadUpload(Shape shape) {
        this.compressed = shape.compressed;
    }

    public static void serverHandle(PayloadUpload message, UUID sender) {
        MakrivaShared.RELAY.maybeAddShape(sender, message.compressed);
    }

    public void decodeFrom(ByteBuf buf) {
        final int len = buf.readInt();
        compressed = new byte[len];
        buf.readBytes(compressed);
    }

    public void encodeInto(ByteBuf buf) {
        buf.writeInt(compressed.length);
        buf.writeBytes(compressed);
    }
}
