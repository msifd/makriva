package msifeed.makriva.mixins.pov;

import msifeed.makriva.MakrivaShared;
import net.minecraft.network.NetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(NetHandlerPlayServer.class)
public class NetHandlerPlayServerMixin {
    /**
     * Replaces fixed hard limit of players height with configurable one
     */
    @ModifyConstant(method = "processPlayer", constant = @Constant(doubleValue = 1.65))
    public double processPlayer(double value) {
        return MakrivaShared.CFG.get().maxEyeHeight;
    }
}
