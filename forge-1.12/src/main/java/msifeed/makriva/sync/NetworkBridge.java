package msifeed.makriva.sync;

import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.Shape;
import msifeed.makriva.storage.CheckedBytes;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;
import java.util.UUID;

public class NetworkBridge implements INetworkBridge {
    private final SimpleNetworkWrapper network = new SimpleNetworkWrapper(MakrivaShared.MOD_ID);

    public NetworkBridge() {
        MinecraftForge.EVENT_BUS.register(this);
        network.registerMessage(MessageDistribute.class, MessageDistribute.class, 0, Side.CLIENT);
        network.registerMessage(MessageUpload.class, MessageUpload.class, 1, Side.SERVER);
    }

    @Override
    public void upload(Shape shape) {
        if (shape == null) return;
        if (Minecraft.getMinecraft().getConnection() == null) return;

        MakrivaShared.LOG.info("Upload shape: {}:{}", shape.name, shape.checksum);
        network.sendToServer(new MessageUpload(shape));
    }

    @Override
    public void relayToAll(Map<UUID, CheckedBytes> encodedShapes) {
        MakrivaShared.LOG.debug("Relay {} shapes to all", encodedShapes.size());

        network.sendToAll(new MessageDistribute(encodedShapes));
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        final Map<UUID, CheckedBytes> encodedShapes = MakrivaShared.RELAY.getEncodedShapes();
        if (encodedShapes.isEmpty()) return;

        MakrivaShared.LOG.debug("Relay {} shapes to {} on login", encodedShapes.size(), event.player.getDisplayNameString());
        network.sendTo(new MessageDistribute(encodedShapes), (EntityPlayerMP) event.player);
    }
}
