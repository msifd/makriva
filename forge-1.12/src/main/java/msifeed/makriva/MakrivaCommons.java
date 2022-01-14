package msifeed.makriva;

import msifeed.makriva.compat.MakrivaCompat;
import msifeed.makriva.model.PlayerPose;
import net.minecraft.client.entity.AbstractClientPlayer;
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
}
