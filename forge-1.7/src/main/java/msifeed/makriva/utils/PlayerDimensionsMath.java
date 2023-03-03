package msifeed.makriva.utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.MakrivaCommons;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.mixins.EntityMixin;
import msifeed.makriva.model.PlayerPose;
import msifeed.makriva.model.SharedShape;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.util.UUID;

public class PlayerDimensionsMath {
    public static void updateBoundingBoxAndEyeHeight(EntityPlayer self) {
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
                }
            }
        }

        final float eyeHeight = MakrivaCommons.calculateEyeHeight(self, shape, pose);
        // Normalize to 1.7 "eye height" definition
        self.eyeHeight = self.getDefaultEyeHeight() - 1.62f + eyeHeight;
    }

    @SideOnly(Side.CLIENT)
    public static float modifyEyeOffsetToOrientCamera(float eyesOffset) {
        final EntityLivingBase entity = Minecraft.getMinecraft().renderViewEntity;
        if (!(entity instanceof EntityPlayer)) return eyesOffset;

        final EntityPlayer player = (EntityPlayer) entity;
        final SharedShape shape = MakrivaShared.SHARED.get(player.getGameProfile().getId());

        final PlayerPose pose = MakrivaCommons.findPose(player);
        final float eyeHeight = MakrivaCommons.calculateEyeHeight(player, shape, pose);

        return eyesOffset + 1.62f - eyeHeight;
    }
}
