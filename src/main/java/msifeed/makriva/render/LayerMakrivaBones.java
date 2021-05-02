package msifeed.makriva.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class LayerMakrivaBones implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer renderer;

    public LayerMakrivaBones(RenderPlayer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!player.hasPlayerInfo() || player.isInvisible()) return;

        final Shape shape = Makriva.SYNC.get(player.getGameProfile().getId());
        if (shape == null) return;
        if (shape.bones.isEmpty()) return;

        renderer.bindTexture(player.getLocationSkin());

    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
