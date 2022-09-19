package msifeed.makriva.utils;

import com.google.common.hash.Hashing;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import org.apache.commons.codec.binary.Hex;

import java.util.Map;

public class MakrivaProfileTexture extends MinecraftProfileTexture {
    private final String hash;

    public MakrivaProfileTexture(String url, Map<String, String> metadata) {
        super(url, metadata);

        final byte[] bytes = Hashing.murmur3_128().hashBytes(url.getBytes()).asBytes();
        hash = Hex.encodeHexString(bytes);
    }

    @Override
    public String getHash() {
        return hash;
    }
}
