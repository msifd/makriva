package msifeed.makriva.expr;

import msifeed.makriva.model.PlayerPose;

public class EvalContextMock implements IEvalContext {
    @Override
    public float playerLimbSwing() {
        return 0;
    }

    @Override
    public float playerPartialTicks() {
        return 0;
    }

    @Override
    public float playerAge() {
        return 0;
    }

    @Override
    public float playerHeadYaw() {
        return 0;
    }

    @Override
    public float playerHeadPitch() {
        return 0;
    }

    @Override
    public float playerScale() {
        return 0;
    }

    @Override
    public boolean hasItemInSlot(InventorySlot slot) {
        return false;
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    public boolean isOverWater() {
        return false;
    }

    @Override
    public boolean isSprinting() {
        return false;
    }

    @Override
    public boolean isRiding() {
        return false;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    public boolean isOnGround() {
        return false;
    }

    @Override
    public boolean isInPose(PlayerPose pose) {
        return false;
    }
}
