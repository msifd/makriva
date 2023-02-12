package msifeed.makriva.storage;

public class CheckedBytes {
    public final byte[] compressed;
    public final long checksum;

    public CheckedBytes(byte[] compressed, long checksum) {
        this.compressed = compressed;
        this.checksum = checksum;
    }
}
