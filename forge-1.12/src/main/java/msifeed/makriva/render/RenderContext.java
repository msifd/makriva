package msifeed.makriva.render;

import msifeed.makriva.expr.IEvalContext;
import msifeed.makriva.model.PlayerPose;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderContext implements IEvalContext {
    public AbstractClientPlayer player = null;
    public PlayerPose currentPose = PlayerPose.stand;

    public float limbSwing;
    public float limbSwingTicks;
    public float partialTicks;
    public float ageInTicks;
    public float netHeadYaw;
    public float headPitch;
    public float scale;

    private static EntityEquipmentSlot convertSlot(InventorySlot slot) {
        switch (slot) {
            default:
            case MAINHAND:
                return EntityEquipmentSlot.MAINHAND;
            case OFFHAND:
                return EntityEquipmentSlot.OFFHAND;
            case HEAD:
                return EntityEquipmentSlot.HEAD;
            case CHEST:
                return EntityEquipmentSlot.CHEST;
            case LEGS:
                return EntityEquipmentSlot.LEGS;
            case FEET:
                return EntityEquipmentSlot.FEET;
        }
    }

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
        return !player.getItemStackFromSlot(convertSlot(slot)).isEmpty();
    }

    @Override
    public boolean isInWater() {
        return player.isInWater();
    }

    @Override
    public boolean isOverWater() {
        return player.isOverWater();
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
            return player.isSneaking();
        return currentPose == pose;
    }
}
