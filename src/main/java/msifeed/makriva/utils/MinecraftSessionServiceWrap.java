package msifeed.makriva.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import msifeed.makriva.data.Shape;
import msifeed.makriva.sync.ShapeSync;

import java.net.InetAddress;
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
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure) {
        final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = inner.getTextures(profile, requireSecure);

        final Shape shape = ShapeSync.get(profile.getId());
        if (shape != null) {
            textures.putAll(shape.textures);
        }

        return textures;
    }

    @Override
    public GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure) {
        return inner.fillProfileProperties(profile, requireSecure);
    }
}
