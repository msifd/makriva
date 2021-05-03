package msifeed.makriva.gui;

import msifeed.makriva.Makriva;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

public class DebugOverlay {
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;

        final UUID uuid = Minecraft.getMinecraft().getSession().getProfile().getId();
        final ModelShape model = Makriva.MODELS.getNullable(uuid);

        final Printer p = new Printer();
        p.print("[Makriva]");
        if (model == null) {
            p.print("Model not created yet");
            return;
        }

        model.shape.textures.forEach((type, tx) -> p.print(type.toString() + ": " + tx.getUrl()));
        p.print("Groups:");
        model.groups.forEach(map -> p.print("  " + String.join(", ", map.keySet())));
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
