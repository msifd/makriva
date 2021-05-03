package msifeed.makriva.data;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import msifeed.makriva.utils.ShapeCodec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

public class Shape {
    public static Shape DEFAULT = new Shape();

    static {
        DEFAULT.updateChecksum();
    }

    public final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = new HashMap<>();
    public final List<Map<String, Bone>> groups = new ArrayList<>();

    public transient long checksum;

    public void updateChecksum() {
        updateChecksum(ShapeCodec.toBytes(this));
    }

    public void updateChecksum(byte[] bytes) {
        final CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        this.checksum = crc32.getValue();
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
}
