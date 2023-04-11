package msifeed.makriva.mixins.pov;

import msifeed.makriva.utils.PlayerDimensionsMath;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {
    @Inject(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/FMLCommonHandler;onPlayerPostTick(Lnet/minecraft/entity/player/EntityPlayer;)V",
                    shift = At.Shift.BEFORE
            )
    )
    public void updateBoundingBoxAndEyeHeight(CallbackInfo ci) {
        final EntityPlayer self = (EntityPlayer) (Object) this;
        PlayerDimensionsMath.updateBoundingBoxAndEyeHeight(self);
    }
}
