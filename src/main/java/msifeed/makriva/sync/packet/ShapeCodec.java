package msifeed.makriva.sync.packet;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import msifeed.makriva.data.Shape;

import java.nio.charset.StandardCharsets;

class ShapeCodec {
    private static final Gson GSON = new Gson();

    public static void writeShapeSpec(ByteBuf buf, Shape shape) {
        final byte[] json = GSON.toJson(shape).getBytes(StandardCharsets.UTF_8);
        buf.writeInt(json.length);
        buf.writeBytes(json);
    }

    public static Shape readShapeSpec(ByteBuf buf) {
        final int len = buf.readInt();
        final String json = buf.toString(buf.readerIndex(), len, StandardCharsets.UTF_8);
        buf.readerIndex(buf.readerIndex() + len);
        return GSON.fromJson(json, Shape.class);
    }
}
