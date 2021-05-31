package msifeed.makriva.data;

import com.google.common.base.Preconditions;
import msifeed.makriva.Makriva;
import msifeed.makriva.expr.IExpr;
import msifeed.makriva.utils.ShapeCodec;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Shape extends SharedShape {
    public static Shape DEFAULT = makeDefaultShape();

    public final Map<String, String> metadata = new HashMap<>();
    public final Map<String, URL> textures = new HashMap<>();
    public final EnumSet<BipedPart> hide = EnumSet.noneOf(BipedPart.class);
    public final Map<BipedPart, IExpr[]> skeleton = new HashMap<>();
    public final List<Bone> bones = new ArrayList<>();

    public final Map<String, IExpr> debug = null;

    public transient String name = "";
    public transient byte[] source;
    public transient long checksum;
    public transient boolean internal = false;

    private static Shape makeDefaultShape() {
        final Shape shape = ShapeCodec.fromBytes("{}".getBytes(StandardCharsets.UTF_8));
        shape.name = "<empty>";
        shape.internal = true;
        return shape;
    }

    public void initBytes(byte[] bytes) {
        this.source = bytes;
        this.checksum = ShapeCodec.checksum(bytes);
    }

    public void validate() throws Exception {
        Preconditions.checkNotNull(metadata);
        Preconditions.checkNotNull(textures);
        Preconditions.checkNotNull(hide);
        Preconditions.checkNotNull(skeleton);
        Preconditions.checkNotNull(eyeHeight);
        Preconditions.checkNotNull(bones);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Shape that = (Shape) o;
        return checksum == that.checksum;
    }

    @Override
    public int hashCode() {
        return (int) (checksum >> Integer.SIZE);
    }

    @Override
    public String toString() {
        return "Shape{" + name + ':' + checksum + '}';
    }

    public Path getShapeFile() throws InvalidPathException {
        return Paths.get(Makriva.MOD_ID).resolve(name + ".json");
    }
}
