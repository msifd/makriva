package msifeed.makriva;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventsHandler {
    @SubscribeEvent
    public void onClientJoin(WorldEvent.Load event) {
        if (!event.getWorld().isRemote || Minecraft.getMinecraft().world != null) return;
        MakrivaShared.MODELS.discardModels();
    }

    @SubscribeEvent
    public void onStartTrackingPlayer(PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof AbstractClientPlayer)) return;
        MakrivaShared.MODELS.invalidateSkin(event.getTarget().getUniqueID());
    }
}
