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

import java.util.Collections;
import java.util.List;
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
    public void relayToAll(UUID uuid, CheckedBytes encodedShape) {
        MakrivaShared.LOG.info("Relay to all from: {} shape: {}", uuid, encodedShape.checksum);
        network.sendToAll(new MessageDistribute(Collections.singletonMap(uuid, encodedShape)));
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        final Map<UUID, CheckedBytes> allShapes = MakrivaShared.RELAY.getEncodedShapes();
        if (allShapes.isEmpty()) return;

        final List<Map<UUID, CheckedBytes>> chunks = PayloadDistribute.splitIntoChunks(allShapes);
        MakrivaShared.LOG.info("Relay {} shapes in {} chunks to {} on login", allShapes.size(), chunks.size(), event.player.getDisplayName());

        chunks.forEach(c -> network.sendTo(new MessageDistribute(c), (EntityPlayerMP) event.player));
    }
}
