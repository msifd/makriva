package msifeed.makriva.gui;

import msifeed.makriva.data.Shape;
import msifeed.makriva.sync.ShapeSync;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DebugOverlay {
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;

        final Shape shape = ShapeSync.get(Minecraft.getMinecraft().getSession().getProfile().getId());

        final Printer p = new Printer();
        if (shape == null) {
            p.print("no data");
            return;
        }

        shape.textures.forEach((type, tx) -> p.print(type.toString() + ": " + tx.getUrl()));
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
