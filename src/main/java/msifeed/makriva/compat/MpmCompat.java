package msifeed.makriva.compat;

import msifeed.makriva.data.PlayerPose;
import net.minecraft.entity.player.EntityPlayer;
import noppes.mpm.ModelData;

import javax.annotation.Nullable;

public class MpmCompat {
    @Nullable
    static PlayerPose getPose(EntityPlayer player) {
        final ModelData data = ModelData.get(player);
        if (data == null) return null;

        switch (data.animation) {
            case SLEEPING_NORTH:
            case SLEEPING_SOUTH:
            case SLEEPING_EAST:
            case SLEEPING_WEST:
                return PlayerPose.sleep;
            case CRAWLING:
                return PlayerPose.crawl;
            case SITTING:
                return PlayerPose.sit;
            default:
                return null;
        }
    }
}
