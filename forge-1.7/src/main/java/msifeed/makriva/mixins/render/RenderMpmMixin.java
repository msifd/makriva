package msifeed.makriva.mixins.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.render.RenderHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import noppes.mpm.client.RenderMPM;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SideOnly(Side.CLIENT)
@Mixin(RenderMPM.class)
public class RenderMpmMixin {
    /**
     * Render shape model hand in the first person and hide original arm if requested
     */
    @Inject(
            method = "renderFirstPersonArm",
            at = @At(value = "INVOKE", target = "Lnoppes/mpm/client/model/ModelMPM;renderArms(Lnet/minecraft/entity/Entity;FZ)V"),
            cancellable = true
    )
    private void renderFirstPersonArm(EntityPlayer player, CallbackInfo ci) {
        final boolean armIsHidden = RenderHandler.renderFirstPersonArm((AbstractClientPlayer) player);
        if (armIsHidden) ci.cancel();
    }
}
