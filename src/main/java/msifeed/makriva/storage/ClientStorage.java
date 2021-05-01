package msifeed.makriva.storage;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import msifeed.makriva.data.Shape;

import java.util.HashMap;
import java.util.Map;

public enum ClientStorage {
    INSTANCE;

    public static Shape getShape() {
        final Shape shape = new Shape();
        final Map<String, String> meta = new HashMap<>();
        meta.put("model", "slim");
        shape.textures.put(MinecraftProfileTexture.Type.SKIN, new MinecraftProfileTexture("https://skins.ariadna.su/Korry.png", meta));

        return shape;
    }
}
