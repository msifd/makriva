package msifeed.makriva.storage;

public class CheckedBytes {
    public final byte[] bytes;
    public final long checksum;

    public CheckedBytes(byte[] bytes, long checksum) {
        this.bytes = bytes;
        this.checksum = checksum;
    }
}
