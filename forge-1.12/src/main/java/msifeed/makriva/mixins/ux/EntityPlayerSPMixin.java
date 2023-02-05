package msifeed.makriva.mixins.ux;

import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.PlayerPose;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin {
    @Shadow
    private boolean serverSneakState;

    @Inject(method = "isSneaking", at = @At("RETURN"), cancellable = true)
    public void doNotLetPlayersToHitTheirHeadWhenStopSneaking(CallbackInfoReturnable<Boolean> cir) {
        final EntityPlayerSP self = (EntityPlayerSP) (Object) this;

        final boolean inputSneak = cir.getReturnValue();
        if (inputSneak || !serverSneakState) return;

        if (willSuffocate(self))
            cir.setReturnValue(true);
    }

    private boolean willSuffocate(EntityPlayerSP self) {
        final float standEyeHeight = MakrivaShared.STORAGE.getCurrentShape().getEyeHeight(PlayerPose.stand);

        final BlockPos.PooledMutableBlockPos pool = BlockPos.PooledMutableBlockPos.retain();

        for (int i = 0; i < 8; ++i) {
            int x = MathHelper.floor(self.posX + (double) ((((i >> 1) % 2) - 0.5F) * self.width * 0.8F));
            int y = MathHelper.floor(self.posY + (double) ((((i >> 0) % 2) - 0.5F) * 0.1F) + standEyeHeight);
            int z = MathHelper.floor(self.posZ + (double) ((((i >> 2) % 2) - 0.5F) * self.width * 0.8F));

            if (pool.getX() != x || pool.getY() != y || pool.getZ() != z) {
                pool.setPos(x, y, z);
                if (self.world.getBlockState(pool).causesSuffocation()) {
                    pool.release();
                    return true;
                }
            }
        }

        pool.release();
        return false;
    }
}
