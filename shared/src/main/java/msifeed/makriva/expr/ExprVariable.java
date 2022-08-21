package msifeed.makriva.expr;

import msifeed.makriva.model.PlayerPose;

import java.util.HashMap;
import java.util.function.Function;

public enum ExprVariable implements IExpr {
    pi(ctx -> ExprVariable.PI),
    rad(ctx -> ExprVariable.RAD),

    limbSwing(IEvalContext::playerLimbSwing),
    limbSwingTick(IEvalContext::playerLimbSwingTicks),
    partialTicks(IEvalContext::playerPartialTicks),
    age(IEvalContext::playerAge),
    headYaw(IEvalContext::playerHeadYaw),
    headPitch(IEvalContext::playerHeadPitch),
    modelScale(IEvalContext::playerScale),

    hasMainhandItem(ctx -> ctx.hasItemInSlot(IEvalContext.InventorySlot.MAINHAND)),
    hasOffhandItem(ctx -> ctx.hasItemInSlot(IEvalContext.InventorySlot.OFFHAND)),
    hasHelmetItem(ctx -> ctx.hasItemInSlot(IEvalContext.InventorySlot.HEAD)),
    hasChestItem(ctx -> ctx.hasItemInSlot(IEvalContext.InventorySlot.CHEST)),
    hasLegsItem(ctx -> ctx.hasItemInSlot(IEvalContext.InventorySlot.LEGS)),
    hasFeetItem(ctx -> ctx.hasItemInSlot(IEvalContext.InventorySlot.FEET)),

    swimming(IEvalContext::isInWater),
    overWater(IEvalContext::isOverWater),
    sprinting(IEvalContext::isSprinting),
    riding(IEvalContext::isRiding),
    burning(IEvalContext::isBurning),
    onGround(IEvalContext::isOnGround),
    dying(IEvalContext::isDying),

    standing(ctx -> ctx.isInPose(PlayerPose.stand)),
    sneaking(ctx -> ctx.isInPose(PlayerPose.sneak)),
    sitting(ctx -> ctx.isInPose(PlayerPose.sit)),
    sleeping(ctx -> ctx.isInPose(PlayerPose.sleep)),
    elytraFlying(ctx -> ctx.isInPose(PlayerPose.elytraFly)),
    crawling(ctx -> ctx.isInPose(PlayerPose.crawl)),
    hugging(ctx -> ctx.isInPose(PlayerPose.hug)),
    dancing(ctx -> ctx.isInPose(PlayerPose.dance)),
    waving(ctx -> ctx.isInPose(PlayerPose.wave)),
    bowing(ctx -> ctx.isInPose(PlayerPose.bow)),
    wagging(ctx -> ctx.isInPose(PlayerPose.wag)),
    crying(ctx -> ctx.isInPose(PlayerPose.cry)),
    pointing(ctx -> ctx.isInPose(PlayerPose.point)),
    yesPose(ctx -> ctx.isInPose(PlayerPose.yes)),
    noPose(ctx -> ctx.isInPose(PlayerPose.no)),
    ;

    private static final float PI = (float) Math.PI;
    private static final float RAD = (float) (180 / Math.PI);
    private static final HashMap<String, ExprVariable> TABLE = new HashMap<>();

    static {
        for (ExprVariable f : values()) {
            if (f.name != null)
                TABLE.put(f.name, f);
        }
    }

    public final String name;
    private final Function<IEvalContext, Object> func;

    ExprVariable(Function<IEvalContext, Object> func) {
        this.name = name();
        this.func = func;
    }

    public static ExprVariable find(String name) {
        return TABLE.get(name);
    }

    @Override
    public boolean asBool(IEvalContext ctx) {
        return (boolean) func.apply(ctx);
    }

    @Override
    public float asFloat(IEvalContext ctx) {
        return (float) func.apply(ctx);
    }
}
