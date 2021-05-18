package msifeed.makriva.compat;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.PlayerPose;
import msifeed.makriva.data.Shape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.mpm.ModelData;

import javax.annotation.Nullable;
import java.util.UUID;

public class MpmCompat {
    @Nullable
    static PlayerPose getPose(EntityPlayer player) {
        final ModelData data = ModelData.get(player);
        if (data == null) return null;

        switch (data.animation) {
            case SLEEPING_NORTH:
            case SLEEPING_SOUTH:
            case SLEEPING_EAST:
            case SLEEPING_WEST:
                return PlayerPose.sleep;
            case CRAWLING:
                return PlayerPose.crawl;
            case SITTING:
                return PlayerPose.sit;
            default:
                return null;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void renderHand(RenderHandEvent event) {
        final AbstractClientPlayer player = Minecraft.getMinecraft().player;
        prioritizeSkin(player);
    }

    @SuppressWarnings("rawtypes")
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void preRender(RenderLivingEvent.Pre event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer)) return;

        final AbstractClientPlayer player = (AbstractClientPlayer) event.getEntity();
        prioritizeSkin(player);
    }

    private static void prioritizeSkin(AbstractClientPlayer player) {
        if (player.ticksExisted > 200) return;

        final UUID uuid = player.getGameProfile().getId();
        if (!Makriva.MODELS.hasShape(uuid)) return;

        final Shape shape = Makriva.MODELS.getShape(uuid);
        if (shape.textures.containsKey("skin")) {
            final ModelData data = ModelData.get(player);
            data.resourceInit = true; // Prioritize makriva skin
        }
    }
}
