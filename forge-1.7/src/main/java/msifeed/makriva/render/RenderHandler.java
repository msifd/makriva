package msifeed.makriva.render;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;

import java.util.UUID;

@SideOnly(Side.CLIENT)
public class RenderHandler {
    @SubscribeEvent
    public void onPreRender(RenderPlayerEvent.Pre event) {
        final AbstractClientPlayer player = (AbstractClientPlayer) event.entityPlayer;
        if (player.isInvisible()) return;

        final UUID uuid = player.getGameProfile().getId();
        final ModelShape model = RenderBridge.getModel(uuid);
        if (model.boxList.isEmpty()) return;

        RenderUtils.updateEvalPlayer(player);
        RenderUtils.updateEvalPartialTicks(event.partialRenderTick);

        model.render = event.renderer;
    }

    public static void onModelRender(AbstractClientPlayer player, float limbSwingTicks, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (player.isInvisible()) return;

        final UUID uuid = player.getGameProfile().getId();
        final ModelShape model = RenderBridge.getModel(uuid);
        if (model.boxList.isEmpty()) return;

        RenderUtils.updateEvalScale(scale);
        model.animationState.update();

//        Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationSkin());
        model.render(player, limbSwingTicks, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }
}
