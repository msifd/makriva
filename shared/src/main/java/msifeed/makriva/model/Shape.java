package msifeed.makriva.model;

import com.google.common.base.Preconditions;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.encoding.ShapeCodec;
import msifeed.makriva.expr.IExpr;

import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Shape extends SharedShape {
    public static Shape DEFAULT = makeDefaultShape();

    public final Map<String, String> metadata = new HashMap<>();
    public final Map<String, IExpr> debug = new HashMap<>();

    public final Map<String, URL> textures = new HashMap<>();
    public final EnumSet<BipedPart> hide = EnumSet.noneOf(BipedPart.class);
    public final Map<BipedPart, float[]> skeleton = new EnumMap<>(BipedPart.class);
    public final AnimationRules animation = new AnimationRules();
    public final List<Bone> bones = new ArrayList<>();
    public int[] textureSize = new int[]{64, 64};

    public transient String name = "";
    public transient byte[] compressed;
    public transient long checksum;
    public transient boolean internal = false;

    private static Shape makeDefaultShape() {
        final Shape shape = ShapeCodec.fromString("{}");
        shape.name = "<empty>";
        shape.internal = true;
        return shape;
    }

    public void initCompressedBytes(byte[] compressed) {
        this.compressed = compressed;
        this.checksum = ShapeCodec.checksum(compressed);
    }

    public void validate() throws Exception {
        Preconditions.checkNotNull(metadata);
        Preconditions.checkNotNull(textures);
        Preconditions.checkNotNull(hide);
        Preconditions.checkNotNull(skeleton);
        Preconditions.checkNotNull(eyeHeight);
        Preconditions.checkNotNull(animation);
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
        return Paths.get(MakrivaShared.MOD_ID).resolve(name + ".json");
    }
}
