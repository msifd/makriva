package msifeed.makriva.encoding;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import msifeed.makriva.expr.IExpr;
import msifeed.makriva.model.AnimationRules;
import msifeed.makriva.model.Shape;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.Adler32;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public final class ShapeCodec {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(IExpr.class, new JsonDeserializerExpr())
            .registerTypeAdapter(AnimationRules.class, new AnimationJsonDeserializer())
            .create();

    public static long checksum(byte[] bytes) {
        final Adler32 checksum = new Adler32();
        checksum.update(bytes);
        return checksum.getValue();
    }

    public static Shape fromString(String source) throws JsonParseException {
        final Shape shape = GSON.fromJson(source, Shape.class);
        if (shape == null) throw new IllegalStateException("Failed to parse shape bytes");

        final byte[] compressed = compress(source.getBytes(StandardCharsets.UTF_8));
        shape.initCompressedBytes(compressed);

        return shape;
    }

    private static byte[] compress(byte[] raw) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(raw.length);
             DeflaterOutputStream zip = new DeflaterOutputStream(bos)) {
            zip.write(raw);
            zip.close();
            bos.close();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to compress shape! Error: " + e.getMessage());
        }
    }

    public static Shape fromCompressed(byte[] compressed) throws JsonParseException, IOException {
        final Reader reader = new BufferedReader(new InputStreamReader(new InflaterInputStream(new ByteArrayInputStream(compressed)), StandardCharsets.UTF_8));
        final Shape shape = GSON.fromJson(reader, Shape.class);
        if (shape == null) throw new IllegalStateException("Failed to parse shape bytes");

        shape.initCompressedBytes(compressed);

        return shape;
    }
}
