package msifeed.makriva.mixins;

import msifeed.makriva.MakrivaCommons;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.PlayerPose;
import msifeed.makriva.model.SharedShape;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.UUID;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {
    /**
     * @author msifeed
     * @reason To categorize by poses
     */
    @Overwrite
    public float getEyeHeight() {
        final EntityPlayer self = (EntityPlayer) (Object) this;

        final UUID uuid = self.getGameProfile().getId();
        final SharedShape shape = MakrivaShared.SHARED.get(uuid);
        final PlayerPose pose = MakrivaCommons.findPose(self);
        return MakrivaCommons.calculateEyeHeight(self, shape, pose);
    }

    /**
     * @author msifeed
     * @reason To categorize by poses
     */
    @Overwrite
    protected void updateSize() {
        final EntityPlayer self = (EntityPlayer) (Object) this;

        final UUID uuid = self.getGameProfile().getId();
        final SharedShape shape = MakrivaShared.SHARED.get(uuid);

        final float[] sizes = shape.getBox(MakrivaCommons.findPose(self));
        if (sizes.length == 2) {
            final float width = sizes[0];
            final float height = sizes[1];

            if (width != self.width || height != self.height) {
                AxisAlignedBB bb = self.getEntityBoundingBox();
                bb = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.minX + width, bb.minY + height, bb.minZ + width);

                if (!self.world.collidesWithAnyBlock(bb)) {
                    ((EntityMixin) self).callSetSize(width, height);
                }
            }
        }

        FMLCommonHandler.instance().onPlayerPostTick(self);
    }
}
