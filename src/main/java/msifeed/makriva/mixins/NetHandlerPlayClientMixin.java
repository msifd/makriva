package msifeed.makriva.mixins;

import msifeed.makriva.Makriva;
import msifeed.makriva.sync.ShapeSync;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketJoinGame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin {
    @Inject(method = "handleJoinGame", at = @At("RETURN"))
    public void handleJoinGame(SPacketJoinGame packetIn, CallbackInfo ci) {
        Makriva.LOG.info("Send my shape");
        ShapeSync.uploadCurrentShape();
    }
}
