package msifeed.makriva.data;

import com.google.common.base.Preconditions;
import msifeed.makriva.expr.IExpr;
import msifeed.makriva.utils.ShapeCodec;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shape {
    public static Shape DEFAULT = ShapeCodec.fromBytes("{}".getBytes(StandardCharsets.UTF_8));

    public final Map<String, String> metadata = new HashMap<>();
    public final Map<String, URL> textures = new HashMap<>();
    public final List<BipedPart> hide = new ArrayList<>();
    public final Map<BipedPart, IExpr[]> skeleton = new HashMap<>();
    public final Map<String, Float> eyeHeight = new HashMap<>();
    public final List<Bone> bones = new ArrayList<>();

    public final Map<String, IExpr> debug = null;

    public transient String name = "";
    public transient byte[] source;
    public transient long checksum;

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
}
