package msifeed.makriva.client;

import msifeed.makriva.Makriva;
import msifeed.makriva.render.model.ModelBone;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;
import java.util.stream.Collectors;

public class DebugOverlay {
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;

        final UUID uuid = Minecraft.getMinecraft().getSession().getProfile().getId();
        final ModelShape model = Makriva.MODELS.getModelWithoutBuild(uuid);

        final Printer p = new Printer();
        p.print("[Makriva]");
        if (model == null) {
            p.print("Model not created yet");
            return;
        }

        model.shape.textures.forEach((key, url) -> p.print(key + ": " + url));
        p.print("Bones: " + model.boxList.stream()
                .filter(b -> b instanceof ModelBone)
                .map(b -> ((ModelBone) b).spec.id)
                .collect(Collectors.joining(", ")));
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
