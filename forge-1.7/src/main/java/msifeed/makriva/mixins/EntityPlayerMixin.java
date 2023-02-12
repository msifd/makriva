package msifeed.makriva.mixins;

import msifeed.makriva.MakrivaCommons;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.PlayerPose;
import msifeed.makriva.model.SharedShape;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

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
    public void updateBoundingBox(CallbackInfo ci) {
        final EntityPlayer self = (EntityPlayer) (Object) this;

        final UUID uuid = self.getGameProfile().getId();
        final SharedShape shape = MakrivaShared.SHARED.get(uuid);
        final PlayerPose pose = MakrivaCommons.findPose(self);

        final float[] sizes = shape.getBox(pose);
        if (sizes.length == 2) {
            final float width = sizes[0];
            final float height = sizes[1];

            if (width != self.width || height != self.height) {
                AxisAlignedBB bb = self.boundingBox;
                bb = AxisAlignedBB.getBoundingBox(bb.minX, bb.minY, bb.minZ, bb.minX + width, bb.minY + height, bb.minZ + width);

                // if not collidesWithAnyBlock
                if (self.worldObj.func_147461_a(bb).isEmpty()) {
                    ((EntityMixin) self).callSetSize(width, height);
                    self.yOffset = height;
                }
            }
        }
    }
}
