package msifeed.makriva.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.PlayerPose;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

@SideOnly(Side.CLIENT)
public class LayerMakrivaBones implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer renderer;

    public LayerMakrivaBones(RenderPlayer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!player.hasPlayerInfo() || player.isInvisible()) return;
//        if (player.ticksExisted < 11) return; // Somehow fixes issue with biped parts rotation (e.g. sneak)

        final UUID uuid = player.getGameProfile().getId();
        final ModelShape model = Makriva.MODELS.getModel(renderer, uuid);
        model.context.renderParams.update(limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        model.context.player = player;
        model.context.pose = PlayerPose.get(player);

        if (model.boxList.isEmpty()) return;

        renderer.bindTexture(player.getLocationSkin());
        model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
