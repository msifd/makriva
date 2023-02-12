package msifeed.makriva.utils;

import com.google.common.hash.Hashing;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import org.apache.commons.codec.binary.Hex;

public class MakrivaProfileTexture extends MinecraftProfileTexture {
    private final String hash;

    public MakrivaProfileTexture(String url) {
        super(url);

        final byte[] bytes = Hashing.murmur3_128().hashBytes(url.getBytes()).asBytes();
        hash = Hex.encodeHexString(bytes);
    }

    @Override
    public String getHash() {
        return hash;
    }
}
