package msifeed.makriva.data;

import msifeed.makriva.sync.packet.ShapeCodec;

import javax.annotation.Nonnull;
import java.util.zip.CRC32;

public class CheckedShape {
    public final Shape shape;
    public final long checksum;

    public CheckedShape(@Nonnull Shape shape) {
        this(shape, ShapeCodec.toBytes(shape));
    }

    public CheckedShape(@Nonnull Shape shape, byte[] srcBytes) {
        this.shape = shape;

        final CRC32 crc32 = new CRC32();
        crc32.update(srcBytes);
        this.checksum = crc32.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CheckedShape that = (CheckedShape) o;
        return checksum == that.checksum;
    }

    @Override
    public int hashCode() {
        return (int) (checksum >> Integer.SIZE);
    }
}
