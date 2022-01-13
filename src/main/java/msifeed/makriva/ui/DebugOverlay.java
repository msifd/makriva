package msifeed.makriva.ui;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.PlayerPose;
import msifeed.makriva.expr.context.EvalContext;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

public class DebugOverlay {
    @SubscribeEvent
    public static void onRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;

        final UUID uuid = Minecraft.getMinecraft().getSession().getProfile().getId();
        final ModelShape model = Makriva.MODELS.getModelWithoutBuild(uuid);
        if (model == null || model.shape.debug.isEmpty()) return;

        final Printer p = new Printer();
        p.print("[Makriva]");
        p.print("Shape: " + model.shape.checksum);
        p.print("Pose: " + PlayerPose.get(Minecraft.getMinecraft().player));
        p.print("Model: " + model.render.getMainModel().getClass().getSimpleName());

        final EvalContext ctx = model.context;
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
