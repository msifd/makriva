package msifeed.makriva.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.expr.IEvalContext;
import msifeed.makriva.model.PlayerPose;
import net.minecraft.client.entity.AbstractClientPlayer;

@SideOnly(Side.CLIENT)
public class RenderContext implements IEvalContext {
    public AbstractClientPlayer player = null;
    public PlayerPose currentPose = PlayerPose.stand;
    public boolean sneaking;
    public float limbSwing;
    public float limbSwingTicks;
    public float partialTicks;
    public float ageInTicks;
    public float netHeadYaw;
    public float headPitch;
    public float scale;

    @Override
    public float playerLimbSwing() {
        return limbSwing;
    }

    @Override
    public float playerLimbSwingTicks() {
        return limbSwingTicks;
    }

    @Override
    public float playerPartialTicks() {
        return partialTicks;
    }

    @Override
    public float playerAge() {
        return ageInTicks;
    }

    @Override
    public float playerHeadYaw() {
        return netHeadYaw;
    }

    @Override
    public float playerHeadPitch() {
        return headPitch;
    }

    @Override
    public float playerScale() {
        return scale;
    }

    @Override
    public boolean hasItemInSlot(InventorySlot slot) {
        switch (slot) {
            case MAINHAND:
            case OFFHAND:
                return player.getHeldItem() != null;
            case HEAD:
                return player.getCurrentArmor(0) != null;
            case CHEST:
                return player.getCurrentArmor(1) != null;
            case LEGS:
                return player.getCurrentArmor(2) != null;
            case FEET:
                return player.getCurrentArmor(3) != null;
        }
        return false;
    }

    @Override
    public boolean isInWater() {
        return player.isInWater();
    }

    @Override
    public boolean isOverWater() {
        // TODO: copy player.isOverWater() implementation from 1.12
        // return player.isOverWater();
        return player.isInWater();
    }

    @Override
    public boolean isSprinting() {
        return player.isSprinting();
    }

    @Override
    public boolean isRiding() {
        return player.isRiding();
    }

    @Override
    public boolean isBurning() {
        return player.isBurning();
    }

    @Override
    public boolean isDying() {
        return player.deathTime > 0;
    }

    @Override
    public boolean isOnGround() {
        return player.onGround;
    }

    @Override
    public boolean isInPose(PlayerPose pose) {
        if (pose == PlayerPose.sneak)
            return sneaking;
        return currentPose == pose;
    }
}
