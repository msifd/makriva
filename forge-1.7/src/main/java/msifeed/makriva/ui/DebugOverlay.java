package msifeed.makriva.ui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.makriva.MakrivaCommons;
import msifeed.makriva.expr.IEvalContext;
import msifeed.makriva.mixins.render.RendererLivingEntityMixin;
import msifeed.makriva.render.RenderBridge;
import msifeed.makriva.render.SharedRenderState;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.UUID;

public class DebugOverlay {
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;

        final UUID uuid = Minecraft.getMinecraft().getSession().func_148256_e().getId();
        final ModelShape model = RenderBridge.getModelWithoutBuild(uuid);
        if (model == null || model.shape.debug.isEmpty()) return;

        final Printer p = new Printer();
        p.print("[Makriva]");
        p.print("Shape: " + model.shape.checksum);
        p.print("Pose: " + MakrivaCommons.findPose(Minecraft.getMinecraft().thePlayer));
        p.print("Model: " + ((RendererLivingEntityMixin) model.render).getMainModel().getClass().getSimpleName());

        final IEvalContext ctx = SharedRenderState.EVAL_CTX;
        model.shape.debug.forEach((s, expr) -> p.print(s + ": " + ctx.num(expr)));
    }

    private static class Printer {
        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        final int x = 10;
        int y = 10;

        void print(String s) {
            fr.drawString(s, x, y, 0xffffffff);
            y += fr.FONT_HEIGHT;
        }
    }
}
