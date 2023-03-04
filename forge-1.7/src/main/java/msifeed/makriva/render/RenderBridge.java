package msifeed.makriva.render;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.mixins.skin.TextureManagerMixin;
import msifeed.makriva.model.Shape;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
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
        return Minecraft.getMinecraft().thePlayer.getUniqueID();
    }

    @Override
    public void updatePlayerEval() {
        RenderUtils.updateEvalPlayer(Minecraft.getMinecraft().thePlayer);
    }

    @Override
    public ModelShape buildModel(Shape shape) {
        final Minecraft mc = Minecraft.getMinecraft();
        final Render render = RenderManager.instance.getEntityClassRenderObject(EntityPlayer.class);

        return new ModelShape((RenderPlayer) render, shape);
    }

    @Override
    public void invalidateAllSkins() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.getNetHandler() == null) return;
        if (mc.theWorld == null) return;

        for (AbstractClientPlayer p : (List<AbstractClientPlayer>) mc.theWorld.playerEntities) {
            this.invalidateSkin(p.getUniqueID());
        }
    }

    @Override
    public void invalidateSkin(UUID uuid) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.getNetHandler() == null) return;
        if (mc.theWorld == null) return;

        if (uuid.equals(mc.thePlayer.getUniqueID())) {
            invalidateSkin(mc.thePlayer);
            return;
        }

        final EntityPlayer player = mc.theWorld.func_152378_a(uuid);
        if (player == null) return;

        invalidateSkin((AbstractClientPlayer) player);
    }

    private void invalidateSkin(AbstractClientPlayer player) {
        final GameProfile profile = player.getGameProfile();
        MakrivaShared.LOG.debug("Invalidate textures of player {}", profile.getName());

        final ResourceLocation defaultSkin = AbstractClientPlayer.locationStevePng;

        // Delete texture from manager to force download
        if (!player.getLocationSkin().equals(defaultSkin)) deleteTexture(player.getLocationSkin());
        deleteTexture(player.getLocationCape());

        // Clear player textures
        player.func_152121_a(MinecraftProfileTexture.Type.SKIN, null);
        player.func_152121_a(MinecraftProfileTexture.Type.CAPE, null);

        // Trigger download
        final SkinManager skinManager = Minecraft.getMinecraft().func_152342_ad();
        skinManager.func_152790_a(profile, player, true);
    }

    private void deleteTexture(@Nullable ResourceLocation res) {
        if (res == null) return;

        final TextureManager textures = Minecraft.getMinecraft().getTextureManager();
        textures.deleteTexture(res);
        ((TextureManagerMixin) textures).getMapTextureObjects().remove(res);
    }
}
