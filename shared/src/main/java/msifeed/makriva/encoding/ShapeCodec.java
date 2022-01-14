package msifeed.makriva.encoding;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import msifeed.makriva.model.AnimationRules;
import msifeed.makriva.model.Shape;
import msifeed.makriva.expr.IExpr;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public final class ShapeCodec {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(IExpr.class, new JsonDeserializerExpr())
            .registerTypeAdapter(AnimationRules.class, new AnimationJsonDeserializer())
            .create();

    public static long checksum(byte[] bytes) {
        final CRC32 checksum = new CRC32();
        checksum.update(bytes);
        return checksum.getValue();
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

    public static Shape fromBytes(byte[] bytes) throws JsonParseException {
        final Shape shape = GSON.fromJson(new String(bytes, StandardCharsets.UTF_8), Shape.class);
        if (shape != null)
            shape.initBytes(bytes);
        return shape;
    }
}
