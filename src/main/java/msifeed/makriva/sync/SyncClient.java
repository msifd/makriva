package msifeed.makriva.sync;

import msifeed.makriva.sync.packet.DistributeMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncClient implements IMessageHandler<DistributeMessage, IMessage> {
    @Override
    public IMessage onMessage(DistributeMessage message, MessageContext ctx) {
        return null;
    }
}
