package msifeed.makriva.mixins.pov;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.PlayerPose;
import msifeed.makriva.render.SharedRenderState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SideOnly(Side.CLIENT)
@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin {
    // FIXME: this whole mixin is not working. fix it

    @Inject(method = "isSneaking", at = @At("RETURN"), cancellable = true)
    public void doNotLetPlayersToHitTheirHeadWhenStopSneaking(CallbackInfoReturnable<Boolean> cir) {
        final EntityPlayerSP self = (EntityPlayerSP) (Object) this;

        final boolean inputSneak = cir.getReturnValue();
        if (inputSneak || !SharedRenderState.EVAL_CTX.isInPose(PlayerPose.sneak)) return;

        if (willSuffocate(self))
            cir.setReturnValue(true);
    }

    private boolean willSuffocate(EntityPlayerSP self) {
        final float standEyeHeight = MakrivaShared.STORAGE.getCurrentShape().getEyeHeight(PlayerPose.stand);

        for (int i = 0; i < 8; ++i) {
            int x = MathHelper.floor_double(self.posX + (double) ((((i >> 1) % 2) - 0.5F) * self.width * 0.8F));
            int y = MathHelper.floor_double(self.posY + (double) ((((i >> 0) % 2) - 0.5F) * 0.1F) + standEyeHeight);
            int z = MathHelper.floor_double(self.posZ + (double) ((((i >> 2) % 2) - 0.5F) * self.width * 0.8F));

            if (self.worldObj.getBlock(x, y, z).isNormalCube()) {
                return true;
            }
        }

        return false;
    }
}
