package msifeed.makriva.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;

import java.net.InetAddress;
import java.net.URL;
import java.util.Map;


public class MinecraftSessionServiceWrap implements MinecraftSessionService {
    private final MinecraftSessionService inner;

    public MinecraftSessionServiceWrap(MinecraftSessionService inner) {
        this.inner = inner;
    }

    @Override
    public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
        inner.joinServer(profile, authenticationToken, serverId);
    }

    @Override
    public GameProfile hasJoinedServer(GameProfile user, String serverId, InetAddress address) throws AuthenticationUnavailableException {
        return inner.hasJoinedServer(user, serverId, address);
    }

    @Override
    public Map<Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure) {
        final Map<Type, MinecraftProfileTexture> textures = inner.getTextures(profile, requireSecure);

        final Shape shape = Makriva.SYNC.get(profile.getId());
        final URL skin = shape.textures.get("skin");
        if (skin != null) textures.put(Type.SKIN, new MinecraftProfileTexture(skin.toString(), shape.metadata));
        final URL cape = shape.textures.get("cape");
        if (cape != null) textures.put(Type.CAPE, new MinecraftProfileTexture(cape.toString(), null));
        final URL elytra = shape.textures.get("elytra");
        if (elytra != null) textures.put(Type.ELYTRA, new MinecraftProfileTexture(elytra.toString(), null));

        return textures;
    }

    @Override
    public GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure) {
        return inner.fillProfileProperties(profile, requireSecure);
    }
}
