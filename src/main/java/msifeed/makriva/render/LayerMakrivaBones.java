package msifeed.makriva.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

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
        if (model.bones.isEmpty()) return;

        renderer.bindTexture(player.getLocationSkin());
        model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
