package msifeed.makriva.expr;

import msifeed.makriva.model.PlayerPose;

public interface IEvalContext {
    float playerLimbSwing();
    float playerLimbSwingTicks();
    float playerPartialTicks();
    float playerAge();
    float playerHeadYaw();
    float playerHeadPitch();
    float playerScale();

    boolean hasItemInSlot(InventorySlot slot);
    boolean isInWater();
    boolean isOverWater();
    boolean isSprinting();
    boolean isRiding();
    boolean isBurning();
    boolean isOnGround();
    boolean isInPose(PlayerPose pose);

    default boolean bool(IExpr expr) {
        return expr.asBool(this);
    }

    default float num(IExpr expr) {
        return expr.asFloat(this);
    }

    enum InventorySlot {
        MAINHAND, OFFHAND, HEAD, CHEST, LEGS, FEET
    }
}
