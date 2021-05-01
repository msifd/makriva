package msifeed.makriva.sync;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import msifeed.makriva.mixins.skin.NetworkPlayerInfoMixin;
import msifeed.makriva.storage.ClientStorage;
import msifeed.makriva.sync.packet.DistributeMessage;
import msifeed.makriva.sync.packet.UploadMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum ShapeSync {
    INSTANCE;

    private final SimpleNetworkWrapper network = new SimpleNetworkWrapper(Makriva.MOD_ID);
    private final Map<UUID, Shape> shapes = new HashMap<>();

    ShapeSync() {
        network.registerMessage(UploadMessage.class, UploadMessage.class, 0, Side.SERVER);
        network.registerMessage(DistributeMessage.class, DistributeMessage.class, 1, Side.CLIENT);
    }

    @Nullable
    public static Shape get(UUID uuid) {
        return INSTANCE.shapes.get(uuid);
    }

    public static Map<UUID, Shape> getShapes() {
        return INSTANCE.shapes;
    }

    public static void uploadCurrentShape() {
        INSTANCE.network.sendToServer(new UploadMessage(ClientStorage.getShape()));
    }

    public static void broadcastShape(UUID uuid, Shape shape) {
        INSTANCE.network.sendToAll(new DistributeMessage(Collections.singletonMap(uuid, shape)));
    }

    @SideOnly(Side.CLIENT)
    public void updateShapes(Map<UUID, Shape> newShapes) {
        final NetHandlerPlayClient conn = Minecraft.getMinecraft().getConnection();
        if (conn == null) return;

        newShapes.forEach((uuid, shape) -> {
            shapes.put(uuid, shape);

            final NetworkPlayerInfo net = conn.getPlayerInfo(uuid);
            if (net != null) {
                final NetworkPlayerInfoMixin mixin = (NetworkPlayerInfoMixin) net;
                mixin.setPlayerTexturesLoaded(false);
            }
        });
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Makriva.LOG.info("Send " + shapes.size() + " shapes to " + event.player.getName());
        network.sendTo(new DistributeMessage(shapes), (EntityPlayerMP) event.player);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientLeave(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Makriva.LOG.info("Shapes cleared");
        shapes.clear();
    }
}