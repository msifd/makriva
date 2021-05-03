package msifeed.makriva.sync;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import msifeed.makriva.sync.packet.DistributeMessage;
import msifeed.makriva.sync.packet.UploadMessage;
import msifeed.makriva.utils.ShapeRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class ShapeSync extends ShapeRegistry<UUID> {
    private final SimpleNetworkWrapper network = new SimpleNetworkWrapper(Makriva.MOD_ID);

    public ShapeSync() {
        network.registerMessage(DistributeMessage.class, DistributeMessage.class, 0, Side.CLIENT);
        network.registerMessage(UploadMessage.class, UploadMessage.class, 1, Side.SERVER);
    }

    public Shape get(UUID uuid) {
        return shapes.getOrDefault(uuid, Shape.DEFAULT);
    }

    public void uploadShape(Shape shape) {
        if (shape != null) {
            Makriva.LOG.info("Upload current shape");
            network.sendToServer(new UploadMessage(shape));
        }
    }

    public void broadcastShape(UUID uuid, Shape shape) {
        network.sendToAll(new DistributeMessage(Collections.singletonMap(uuid, shape)));
    }

    public void maybeAddShape(UUID uuid, Shape shape) {
        if (!isKnownShape(uuid, shape)) {
            addShape(uuid, shape);
            broadcastShape(uuid, shape);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!shapes.isEmpty()) {
            network.sendTo(new DistributeMessage(shapes), (EntityPlayerMP) event.player);
        }
    }

    @SideOnly(Side.CLIENT)
    public void updateShapes(Map<UUID, Shape> newShapes) {
        newShapes.forEach((uuid, shape) -> {
//            if (isKnownShape(uuid, shape)) return;

            shapes.put(uuid, shape);
            Makriva.MODELS.invalidate(uuid);
        });
    }
}
