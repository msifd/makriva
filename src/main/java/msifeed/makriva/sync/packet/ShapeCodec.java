package msifeed.makriva.sync.packet;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import msifeed.makriva.data.CheckedShape;
import msifeed.makriva.data.Shape;

import java.nio.charset.StandardCharsets;

public final class ShapeCodec {
    private static final Gson GSON = new Gson();

    public static void writeShape(ByteBuf buf, Shape shape) {
        final byte[] json = toBytes(shape);
        buf.writeInt(json.length);
        buf.writeBytes(json);
    }

    public static Shape readShape(ByteBuf buf) {
        final int len = buf.readInt();
        final String json = buf.toString(buf.readerIndex(), len, StandardCharsets.UTF_8);
        buf.readerIndex(buf.readerIndex() + len);
        return GSON.fromJson(json, Shape.class);
    }

    public static CheckedShape readShapeToChecked(ByteBuf buf) {
        final int len = buf.readInt();
        final byte[] bytes = ByteBufUtil.getBytes(buf, buf.readerIndex(), len);
        final String json = new String(bytes, StandardCharsets.UTF_8);
        final Shape shape = GSON.fromJson(json, Shape.class);
        return new CheckedShape(shape, bytes);
    }

    public static byte[] toBytes(Shape shape) {
        return GSON.toJson(shape).getBytes(StandardCharsets.UTF_8);
    }
}
