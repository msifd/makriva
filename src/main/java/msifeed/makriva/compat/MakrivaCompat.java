package msifeed.makriva.compat;

import msifeed.makriva.data.PlayerPose;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;

public class MakrivaCompat {
    public static boolean mpm = false;

    @Nullable
    public static PlayerPose getPose(EntityPlayer player) {
        if (mpm) return MpmCompat.getPose(player);
        return null;
    }
}
