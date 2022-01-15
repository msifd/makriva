package msifeed.makriva.sync;

import msifeed.makriva.Makriva;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.Shape;
import msifeed.makriva.model.SharedShape;
import msifeed.makriva.storage.AbstractShapeRelay;
import msifeed.makriva.storage.CheckedBytes;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;
import java.util.UUID;

public class SyncRelay extends AbstractShapeRelay implements IMessageHandler<MessageUpload, IMessage> {
    private final SimpleNetworkWrapper network = new SimpleNetworkWrapper(MakrivaShared.MOD_ID);

    public static void upload(Shape shape) {
        if (shape == null) return;
        if (Minecraft.getMinecraft().getConnection() == null) return;

        MakrivaShared.LOG.info("Upload shape: {}:{}", shape.name, shape.checksum);
        Makriva.RELAY.network.sendToServer(new MessageUpload(shape));
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
        network.registerMessage(MessageDistribute.class, MessageDistribute.class, 0, Side.CLIENT);
        network.registerMessage(Makriva.RELAY, MessageUpload.class, 1, Side.SERVER);
    }

    @Override
    protected void relayAll(Map<UUID, CheckedBytes> toRelay) {
        network.sendToAll(new MessageDistribute(toRelay));
    }

    @Override
    protected void updateSharedData(UUID uuid, SharedShape shared) {
        Makriva.SHARED.update(uuid, shared);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!encodedShapes.isEmpty()) {
            network.sendTo(new MessageDistribute(encodedShapes), (EntityPlayerMP) event.player);
        }
    }

    @Override
    public IMessage onMessage(MessageUpload message, MessageContext ctx) {
        if (message.shapeBytes == null || message.shapeBytes.length == 0) return null;

        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
            final UUID uuid = ctx.getServerHandler().player.getGameProfile().getId();
            maybeAddShape(uuid, message.shapeBytes);
        });

        return null;
    }
}
