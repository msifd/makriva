package msifeed.makriva.data;

import net.minecraft.entity.player.EntityPlayer;

public enum PlayerPose {
    stand, sneak, sleep, elytraFly;

    public static PlayerPose get(EntityPlayer player) {
        if (player.isElytraFlying())
            return elytraFly;
        else if (player.isPlayerSleeping())
            return sleep;
        else if (player.isSneaking())
            return sneak;
        else
            return stand;
    }
}
