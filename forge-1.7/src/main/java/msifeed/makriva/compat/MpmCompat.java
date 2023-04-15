package msifeed.makriva.compat;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.BipedPart;
import msifeed.makriva.model.PlayerPose;
import msifeed.makriva.model.Shape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.data.ModelData;
import noppes.mpm.data.PlayerDataController;

import javax.annotation.Nullable;
import java.util.UUID;

public class MpmCompat {
    public static String SKIN_URL = null;
    private static byte headWearType = 0;

    @Nullable
    static PlayerPose getPose(EntityPlayer player) {
        final ModelData data = PlayerDataController.instance.getPlayerData(player);
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
            case HUG:
                return PlayerPose.hug;
            case DANCING:
                return PlayerPose.dance;
            case WAVING:
                return PlayerPose.wave;
            case BOW:
                return PlayerPose.bow;
            case WAG:
                return PlayerPose.wag;
            case CRY:
                return PlayerPose.cry;
            default:
                return null;
        }
    }

    static float getEyeHeightOffset(EntityPlayer player, PlayerPose pose) {
        if (pose == PlayerPose.sleep || pose == PlayerPose.crawl) return 0;

        final ModelData data = PlayerDataController.instance.getPlayerData(player);
        if (data == null) return 0;

        return -getOffsetCamera(data, player);
    }

    private static float getOffsetCamera(ModelData data, EntityPlayer player) {
        if (!MorePlayerModels.EnablePOV) {
            return 0.0F;
        } else {
            float offset = -data.offsetY();
            if (data.animation == EnumAnimation.SITTING) {
                offset += 0.5F - data.getLegsY();
            }

            if (data.isSleeping()) {
                offset = 1.18F;
            }

            if (data.animation == EnumAnimation.CRAWLING) {
                offset = 0.8F;
            }

            if (offset < -0.2F && isBlocked(player)) {
                offset = -0.2F;
            }

            return offset;
        }
    }

    private static boolean isBlocked(EntityPlayer player) {
        final ChunkCoordinates cord = player.getPlayerCoordinates();
        return !player.worldObj.isAirBlock(cord.posX, cord.posY + 2, cord.posZ);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void renderHand(RenderHandEvent event) {
        final AbstractClientPlayer player = Minecraft.getMinecraft().thePlayer;
        final UUID uuid = player.getGameProfile().getId();
        if (!MakrivaShared.MODELS.hasShape(uuid)) return;

        final Shape shape = MakrivaShared.MODELS.getShape(uuid);
        prioritizeSkin(player, shape);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void preRender(RenderLivingEvent.Pre event) {
        if (!(event.entity instanceof AbstractClientPlayer)) return;

        final AbstractClientPlayer player = (AbstractClientPlayer) event.entity;
        final @Nullable ModelData data = PlayerDataController.instance.getPlayerData(player);

        // Remember head wear setting ...
        headWearType = data != null ? data.headwear : 0;

        final UUID uuid = player.getGameProfile().getId();
        if (!MakrivaShared.MODELS.hasShape(uuid)) return;

        final Shape shape = MakrivaShared.MODELS.getShape(uuid);
        prioritizeSkin(player, shape);

        // ... and disable head wear
        if (data != null && shape.hide.contains(BipedPart.head)) {
            data.headwear = 0;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void postRender(RenderLivingEvent.Post event) {
        if (!(event.entity instanceof AbstractClientPlayer)) return;

        final AbstractClientPlayer player = (AbstractClientPlayer) event.entity;
        final ModelData data = PlayerDataController.instance.getPlayerData(player);

        // Restore headwear setting
        if (data != null) data.headwear = headWearType;
    }

    @SideOnly(Side.CLIENT)
    private void prioritizeSkin(AbstractClientPlayer player, Shape shape) {
//        if (player.ticksExisted > 200) return;
//
//        final ModelData data = PlayerDataController.instance.getPlayerData(player);
//
//        if (shape.textures.containsKey("skin")) {
//            data.loaded = true; // Prioritize makriva skin
//        } else if (player.ticksExisted == 100) {
//            data.loaded = false; // Fix too-fast skin load by loading the skin again
//        }
    }

    @SideOnly(Side.CLIENT)
    public static void reloadSkin(AbstractClientPlayer player) {
        final ModelData data = PlayerDataController.instance.getPlayerData(player);
        data.loaded = false;
    }
}
