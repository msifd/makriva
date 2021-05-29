package msifeed.makriva.compat;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.BipedPart;
import msifeed.makriva.data.PlayerPose;
import msifeed.makriva.data.Shape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;

import javax.annotation.Nullable;
import java.util.UUID;

public class MpmCompat {
    private static int headWearType = 0;

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

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void renderHand(RenderHandEvent event) {
        final AbstractClientPlayer player = Minecraft.getMinecraft().player;
        final UUID uuid = player.getGameProfile().getId();
        if (!Makriva.MODELS.hasShape(uuid)) return;
        final Shape shape = Makriva.MODELS.getShape(uuid);

        prioritizeSkin(player, shape);
    }

    @SuppressWarnings("rawtypes")
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void preRender(RenderLivingEvent.Pre event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer)) return;

        final AbstractClientPlayer player = (AbstractClientPlayer) event.getEntity();
        final UUID uuid = player.getGameProfile().getId();
        if (!Makriva.MODELS.hasShape(uuid)) return;
        final Shape shape = Makriva.MODELS.getShape(uuid);

        prioritizeSkin(player, shape);

        // Remember setting and disable headwear
        headWearType = MorePlayerModels.HeadWearType;
        if (shape.hide.contains(BipedPart.head)) {
            MorePlayerModels.HeadWearType = 0;
        }
    }

    @SuppressWarnings("rawtypes")
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void postRender(RenderLivingEvent.Post event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer)) return;

        // Restore headwear setting
        MorePlayerModels.HeadWearType = headWearType;
    }

    @SideOnly(Side.CLIENT)
    private static void prioritizeSkin(AbstractClientPlayer player, Shape shape) {
        if (player.ticksExisted > 200) return;

        if (shape.textures.containsKey("skin")) {
            final ModelData data = ModelData.get(player);
            data.resourceInit = true; // Prioritize makriva skin
        }
    }
}
