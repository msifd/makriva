package msifeed.makriva;

import msifeed.makriva.compat.MakrivaCompat;
import msifeed.makriva.model.PlayerPose;
import msifeed.makriva.model.SharedShape;
import net.minecraft.entity.player.EntityPlayer;

public class MakrivaCommons {
    public static PlayerPose findPose(EntityPlayer player) {
        if (player.isElytraFlying())
            return PlayerPose.elytraFly;
        else if (player.isPlayerSleeping())
            return PlayerPose.sleep;
        else if (player.isRiding())
            return PlayerPose.sit;

        final PlayerPose compat = MakrivaCompat.getPose(player);
        if (compat != null)
            return compat;

        if (player.isSneaking())
            return PlayerPose.sneak;

        return PlayerPose.stand;
    }

    public static float calculateEyeHeight(EntityPlayer player, SharedShape shape, PlayerPose pose) {
        float height = shape.getEyeHeight(pose);
        if (shape.eyeHeight.isEmpty()) {
            height += MakrivaCompat.getEyeHeightOffset(player, pose);
        }
        return height;
    }
}
