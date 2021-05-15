package msifeed.makriva.sync;

import com.google.gson.Gson;
import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import msifeed.makriva.data.SharedShape;
import msifeed.makriva.utils.ShapeCodec;
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

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SyncRelay implements IMessageHandler<MessageUpload, IMessage> {
    private final SimpleNetworkWrapper network = new SimpleNetworkWrapper(Makriva.MOD_ID);
    private final Map<UUID, CheckedBytes> encodedShapes = new HashMap<>();
    private final Gson gson = new Gson();

    public static void upload(Shape shape) {
        if (shape != null) {
            Makriva.LOG.info("Upload shape: {}:{}", shape.name, shape.checksum);
            Makriva.RELAY.network.sendToServer(new MessageUpload(shape));
        }
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);

        network.registerMessage(MessageDistribute.class, MessageDistribute.class, 0, Side.CLIENT);
        network.registerMessage(Makriva.RELAY, MessageUpload.class, 1, Side.SERVER);
    }

    private void maybeAddShape(UUID uuid, byte[] shapeBytes) {
        final long checksum = ShapeCodec.checksum(shapeBytes);
        if (!isKnownShape(uuid, checksum)) {
            final CheckedBytes checked = new CheckedBytes(shapeBytes, checksum);
            encodedShapes.put(uuid, checked);
            network.sendToAll(new MessageDistribute(Collections.singletonMap(uuid, checked)));
            updateSharedShape(uuid, shapeBytes);
        }
    }

    private boolean isKnownShape(UUID uuid, long checksum) {
        final CheckedBytes local = encodedShapes.get(uuid);
        if (local == null) return false;
        return local.checksum == checksum;
    }

    private void updateSharedShape(UUID uuid, byte[] shapeBytes) {
        final String str = new String(shapeBytes, StandardCharsets.UTF_8);
        try {
            final SharedShape shape = gson.fromJson(str, SharedShape.class);
            Makriva.SHARED_SHAPES.updateShape(uuid, shape);
        } catch (Exception ignored) {
        }
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

    public static class CheckedBytes {
        public final byte[] bytes;
        public final long checksum;

        public CheckedBytes(byte[] bytes, long checksum) {
            this.bytes = bytes;
            this.checksum = checksum;
        }
    }
}
