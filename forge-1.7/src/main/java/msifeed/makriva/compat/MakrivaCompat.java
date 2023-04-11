package msifeed.makriva.compat;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.model.PlayerPose;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;

public class MakrivaCompat {
    public static final Marker COMPAT = MarkerManager.getMarker("Compat");
    public static boolean mpm = false;

    @Nullable
    public static PlayerPose getPose(EntityPlayer player) {
        if (mpm) return MpmCompat.getPose(player);
        return null;
    }

    public static float getEyeHeightOffset(EntityPlayer player, PlayerPose pose) {
        if (mpm) return MpmCompat.getEyeHeightOffset(player, pose);
        return 0;
    }

    @SideOnly(Side.CLIENT)
    private static void invalidateSkin(AbstractClientPlayer player) {
        if (mpm) MpmCompat.reloadSkin(player);
    }
}
