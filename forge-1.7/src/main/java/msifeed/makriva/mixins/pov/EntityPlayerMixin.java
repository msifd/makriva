package msifeed.makriva.mixins.pov;

import msifeed.makriva.utils.PlayerDimensionsMath;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {
    /**
     * @author msifeed
     * @reason To categorize by poses
     */
//    @Overwrite
//    public float getEyeHeight() {
//        final EntityPlayer self = (EntityPlayer) (Object) this;
//
//        final UUID uuid = self.getGameProfile().getId();
//        final SharedShape shape = MakrivaShared.SHARED.get(uuid);
//        final PlayerPose pose = MakrivaCommons.findPose(self);
//        final float eyeHeight = MakrivaCommons.calculateEyeHeight(self, shape, pose);
//
//        // Normalize to 1.7 "eye height" definition
//        return eyeHeight - 1.62f + self.getDefaultEyeHeight();
//    }

    @Inject(
            method = "onUpdate",
            at = @At(value = "INVOKE", target = "Lcpw/mods/fml/common/FMLCommonHandler;onPlayerPostTick(Lnet/minecraft/entity/player/EntityPlayer;)V")
    )
    public void updateBoundingBoxAndEyeHeight(CallbackInfo ci) {
        final EntityPlayer self = (EntityPlayer) (Object) this;
        PlayerDimensionsMath.updateBoundingBoxAndEyeHeight(self);
    }
}
