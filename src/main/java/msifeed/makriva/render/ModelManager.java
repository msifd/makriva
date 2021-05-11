package msifeed.makriva.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import msifeed.makriva.mixins.render.RenderManagerMixin;
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
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ModelManager {
    private final Map<UUID, Shape> shapes = new HashMap<>();
    private final Map<UUID, ModelShape> models = new HashMap<>();

    private ModelShape previewModel = null;

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

    @Nonnull
    public Shape getShape(UUID uuid) {
        return previewModel != null
                ? previewModel.shape
                : shapes.getOrDefault(uuid, Shape.DEFAULT);
    }

    @Nonnull
    public ModelShape getModel(RenderPlayer render, UUID uuid) {
        return previewModel != null
                ? previewModel
                : models.computeIfAbsent(uuid, id -> buildModel(render, uuid));
    }

    @Nullable
    public ModelShape getModelWithoutBuild(UUID uuid) {
        return previewModel != null
                ? previewModel
                : models.get(uuid);
    }

    public void updateShape(UUID uuid, Shape shape) {
        invalidate(uuid);
        shapes.put(uuid, shape);
    }

    public void invalidate(UUID uuid) {
        invalidateSkin(uuid);
        models.remove(uuid);
    }

    public void selectPreview(String name) {
        final Shape shape = Makriva.STORAGE.getShapes().get(name);
        if (shape == null) {
            Makriva.LOG.warn("Can't preview unknown shape: {}", name);
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();
        final RenderPlayer renderManager = ((RenderManagerMixin) mc.getRenderManager()).getPlayerRenderer();
        previewModel = new ModelShape(renderManager, shape);
        previewModel.context.player = mc.player;

        invalidateSkin(mc.player.getUniqueID());
    }

    public void clearPreview() {
        previewModel = null;
        invalidateSkin(Minecraft.getMinecraft().player.getUniqueID());
    }

    private void invalidateSkin(UUID uuid) {
        final NetHandlerPlayClient conn = Minecraft.getMinecraft().getConnection();
        if (conn == null) return;

        final NetworkPlayerInfo net = conn.getPlayerInfo(uuid);
        if (net != null) {
            final NetworkPlayerInfoMixin mixin = (NetworkPlayerInfoMixin) net;
            mixin.setPlayerTexturesLoaded(false);
        }
    }

    private ModelShape buildModel(RenderPlayer render, UUID uuid) {
        final Shape shape = getShape(uuid);
        Makriva.LOG.info("Build shape model uuid: {}, checksum: {}", uuid, shape.checksum);
        return new ModelShape(render, shape);
    }

    @SubscribeEvent
    public void onClientJoin(WorldEvent.Load event) {
        if (event.getWorld().isRemote && Minecraft.getMinecraft().world == null) {
            Makriva.LOG.info("Discard all shape models");
            models.clear();
            previewModel = null;
        }
    }
}
