package msifeed.makriva.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import msifeed.makriva.mixins.skin.MinecraftAssetsMixin;
import msifeed.makriva.mixins.skin.NetworkPlayerInfoMixin;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ShapeModels {
    protected final Map<UUID, ModelShape> models = new HashMap<>();

    private static ModelShape build(RenderPlayer render, UUID uuid) {
        final Shape shape = Makriva.SYNC.get(uuid);
        Makriva.LOG.info("Build shape model uuid: {}, checksum: {}", uuid, shape.checksum);

        return new ModelShape(render, shape);
    }

    public ModelShape getOrCreate(RenderPlayer render, UUID uuid) {
        return models.computeIfAbsent(uuid, id -> build(render, uuid));
    }

    @Nullable
    public ModelShape getNullable(UUID uuid) {
        return models.get(uuid);
    }

    public void invalidate(UUID uuid) {
        models.remove(uuid);

        final NetHandlerPlayClient conn = Minecraft.getMinecraft().getConnection();
        if (conn == null) return;

        final NetworkPlayerInfo net = conn.getPlayerInfo(uuid);
        if (net != null) {
            final NetworkPlayerInfoMixin mixin = (NetworkPlayerInfoMixin) net;
            mixin.setPlayerTexturesLoaded(false);
        }
    }

    public static void loadTexture(ResourceLocation resource, String cacheName, URL url) {
        final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        final ITextureObject textureObject = textureManager.getTexture(resource);
        if (textureObject == null) {
            final MinecraftAssetsMixin assetsMixin = (MinecraftAssetsMixin) Minecraft.getMinecraft();
            final File makrivaDir = new File(assetsMixin.getFileAssets(), Makriva.MOD_ID);
            final File cacheFile = new File(makrivaDir, cacheName);

            final ThreadDownloadImageData loader = new ThreadDownloadImageData(cacheFile, url.toString(), DefaultPlayerSkin.getDefaultSkinLegacy(), new ImageBufferDownload());
            textureManager.loadTexture(resource, loader);
        }
    }
}
