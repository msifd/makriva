package msifeed.makriva.render;

import msifeed.makriva.MakrivaShared;
import msifeed.makriva.mixins.render.RenderManagerMixin;
import msifeed.makriva.mixins.skin.NetworkPlayerInfoGetter;
import msifeed.makriva.model.Shape;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class RenderBridge implements IRenderBridge<ModelShape> {
    public static ModelShape getModel(UUID uuid) {
        return (ModelShape) MakrivaShared.MODELS.getModel(uuid);
    }

    @Nullable
    public static ModelShape getModelWithoutBuild(UUID uuid) {
        return (ModelShape) MakrivaShared.MODELS.getModelWithoutBuild(uuid);
    }

    @Nullable
    public static ModelShape getPreviewModel() {
        return (ModelShape) MakrivaShared.MODELS.getPreviewModel();
    }

    @Override
    public UUID getPlayerUuid() {
        return Minecraft.getMinecraft().player.getUniqueID();
    }

    @Override
    public void updatePlayerEval() {
        RenderUtils.updateEvalPlayer(Minecraft.getMinecraft().player);
    }

    @Override
    public ModelShape buildModel(Shape shape) {
        final Minecraft mc = Minecraft.getMinecraft();
        final RenderPlayer render = ((RenderManagerMixin) mc.getRenderManager()).getPlayerRenderer();
        return new ModelShape(render, shape);
    }

    @Override
    public void invalidateAllSkins() {
        final NetHandlerPlayClient conn = Minecraft.getMinecraft().getConnection();
        if (conn == null) return;

        for (NetworkPlayerInfo net : conn.getPlayerInfoMap()) {
            this.invalidateSkin(net);
        }
    }

    @Override
    public void invalidateSkin(UUID uuid) {
        final Minecraft mc = Minecraft.getMinecraft();
        final NetHandlerPlayClient conn = mc.getConnection();
        if (conn == null) return;

        final NetworkPlayerInfo net = conn.getPlayerInfo(uuid);
        if (net != null) {
            this.invalidateSkin(net);
            net.getLocationSkin(); // Calls loadPlayerTextures inside
        }
    }

    private void invalidateSkin(@Nonnull NetworkPlayerInfo net) {
        final NetworkPlayerInfoGetter mixin = (NetworkPlayerInfoGetter) net;
        final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        MakrivaShared.LOG.debug("Invalidate textures of player {}", net.getGameProfile().getName());
        mixin.getPlayerTextures().forEach((type, res) -> textureManager.deleteTexture(res));
        mixin.getPlayerTextures().clear();
        mixin.setPlayerTexturesLoaded(false);
    }
}
