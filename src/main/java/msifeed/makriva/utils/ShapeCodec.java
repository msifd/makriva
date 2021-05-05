package msifeed.makriva.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import msifeed.makriva.data.Shape;
import msifeed.makriva.expr.IExpr;
import msifeed.makriva.expr.json.JsonAdapterExpr;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

public final class ShapeCodec {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(IExpr.class, new JsonAdapterExpr())
            .create();

    public static void writeShape(ByteBuf buf, Shape shape) {
        final byte[] json = toBytes(shape);
        buf.writeInt(json.length);
        buf.writeBytes(json);
    }

    @Nullable
    public static Shape readShape(ByteBuf buf) {
        final int len = buf.readInt();
        final byte[] bytes = ByteBufUtil.getBytes(buf, buf.readerIndex(), len);
        buf.readerIndex(buf.readerIndex() + len);

        try {
            return ShapeCodec.fromBytes(bytes);
        } catch (JsonParseException e) {
            return null;
        }
    }

    public static byte[] toBytes(Shape shape) {
        return GSON.toJson(shape).getBytes(StandardCharsets.UTF_8);
    }

    public static Shape fromBytes(byte[] bytes) throws JsonParseException {
        final Shape shape = GSON.fromJson(new String(bytes, StandardCharsets.UTF_8), Shape.class);
        if (shape != null)
            shape.updateChecksum(bytes);
        return shape;
    }
}
