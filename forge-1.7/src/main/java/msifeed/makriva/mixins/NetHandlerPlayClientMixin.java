package msifeed.makriva.mixins;

import msifeed.makriva.MakrivaShared;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin {
    @Inject(method = "handleJoinGame", at = @At("RETURN"))
    public void handleJoinGame(S01PacketJoinGame packetIn, CallbackInfo ci) {
        MakrivaShared.RELAY.upload(MakrivaShared.STORAGE.getCurrentShape());
    }
}
