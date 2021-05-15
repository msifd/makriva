package msifeed.makriva.mixins;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.PlayerPose;
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
     */
    @Overwrite
    public float getEyeHeight() {
        final EntityPlayer self = (EntityPlayer) (Object) this;

        final UUID uuid = self.getGameProfile().getId();
        final PlayerPose pose = PlayerPose.get(self);
        return Makriva.SHARED_SHAPES.getShape(uuid).getEyeHeight(pose);
    }

    /**
     * @author msifeed
     */
    @Overwrite
    protected void updateSize() {
        final EntityPlayer self = (EntityPlayer) (Object) this;

        final UUID uuid = self.getGameProfile().getId();
        final PlayerPose pose = PlayerPose.get(self);
        final Float[] sizes = Makriva.SHARED_SHAPES.getShape(uuid).getBox(pose);

        if (sizes[0] != self.width || sizes[1] != self.height) {
            AxisAlignedBB bb = self.getEntityBoundingBox();
            bb = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.minX + sizes[0], bb.minY + sizes[1], bb.minZ + sizes[0]);

            if (!self.world.collidesWithAnyBlock(bb)) {
                ((EntityMixin) self).callSetSize(sizes[0], sizes[1]);
            }
        }

        FMLCommonHandler.instance().onPlayerPostTick(self);
    }
}
