package msifeed.makriva.data;

import msifeed.makriva.compat.MakrivaCompat;
import net.minecraft.entity.player.EntityPlayer;

public enum PlayerPose {
    stand, sneak, sit, sleep, elytraFly, crawl,
    ;

    public static PlayerPose get(EntityPlayer player) {
        if (player.isElytraFlying())
            return elytraFly;
        else if (player.isPlayerSleeping())
            return sleep;
        else if (player.isRiding())
            return sit;

        final PlayerPose compat = MakrivaCompat.getPose(player);
        if (compat != null)
            return compat;

        if (player.isSneaking())
            return sneak;

        return stand;
    }
}
