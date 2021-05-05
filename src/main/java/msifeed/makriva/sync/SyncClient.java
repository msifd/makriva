package msifeed.makriva.sync;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class SyncClient implements IMessageHandler<MessageDistribute, IMessage> {

    private final Map<UUID, Shape> shapes = new HashMap<>();

    public Shape get(UUID uuid) {
        return shapes.getOrDefault(uuid, Shape.DEFAULT);
    }

    @Override
    public IMessage onMessage(MessageDistribute message, MessageContext ctx) {
        if (message.shapes == null) return null;
        Makriva.LOG.info("Received " + message.shapes.size() + " shapes");

        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
            updateShapes(message.shapes);
        });

        return null;
    }

    private void updateShapes(Map<UUID, Shape> newShapes) {
        newShapes.forEach((uuid, shape) -> {
            shapes.put(uuid, shape);
            Makriva.MODELS.invalidate(uuid);
        });
    }
}
