package msifeed.makriva.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class LayerMakrivaBones implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer renderer;

    public LayerMakrivaBones(RenderPlayer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!player.hasPlayerInfo() || player.isInvisible()) return;

        final UUID uuid = player.getGameProfile().getId();
        final ModelShape model = Makriva.MODELS.getOrCreate(renderer, uuid);
        if (model.groups.isEmpty()) return;

//        float f = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks - (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks);
//        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        GlStateManager.pushMatrix();
//        GlStateManager.rotate(f, 0.0F, 1.0F, 0.0F);
//        GlStateManager.rotate(f1, 1.0F, 0.0F, 0.0F);
//        GlStateManager.translate(0.375F * (float)(0 * 2 - 1), 0.0F, 0.0F);
//        GlStateManager.translate(0.0F, -0.375F, 0.0F);
//        GlStateManager.rotate(-f1, 1.0F, 0.0F, 0.0F);
//        GlStateManager.rotate(-f, 0.0F, 1.0F, 0.0F);
//        float f2 = 1.3333334F;
//        GlStateManager.scale(f2, f2, f2);

//        renderer.bindTexture(player.getLocationSkin());
        renderer.bindTexture(new ResourceLocation("asd", "asd"));
        model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
