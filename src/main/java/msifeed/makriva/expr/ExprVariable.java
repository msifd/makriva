package msifeed.makriva.expr;

import msifeed.makriva.data.PlayerPose;
import msifeed.makriva.expr.context.EvalContext;
import net.minecraft.inventory.EntityEquipmentSlot;

import java.util.HashMap;
import java.util.function.Function;

public enum ExprVariable implements IExpr {
    pi(ctx -> ExprVariable.PI),

    limbSwingTick(ctx -> ctx.renderParams.limbSwingTick),
    limbSwing(ctx -> ctx.renderParams.limbSwing),
    partialTicks(ctx -> ctx.renderParams.partialTicks),
    age(ctx -> ctx.renderParams.ageInTicks),
    netHeadYaw(ctx -> ctx.renderParams.netHeadYaw),
    headPitch(ctx -> ctx.renderParams.headPitch),
    modelScale(ctx -> ctx.renderParams.scale),

    hasMainhandItem(ctx -> !ctx.player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).isEmpty()),
    hasOffhandItem(ctx -> !ctx.player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).isEmpty()),
    hasHelmetItem(ctx -> !ctx.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()),
    hasChestItem(ctx -> !ctx.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty()),
    hasLegsItem(ctx -> !ctx.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).isEmpty()),
    hasFeetItem(ctx -> !ctx.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).isEmpty()),

    swimming(ctx -> ctx.player.isInWater()),
    overWater(ctx -> ctx.player.isOverWater()),
    sprinting(ctx -> ctx.player.isSprinting()),
    riding(ctx -> ctx.player.isRiding()),
    burning(ctx -> ctx.player.isBurning()),
    onGround(ctx -> ctx.player.onGround),

    standing(ctx -> ctx.pose == PlayerPose.stand),
    sneaking(ctx -> ctx.pose == PlayerPose.sneak),
    sitting(ctx -> ctx.pose == PlayerPose.sit),
    sleeping(ctx -> ctx.pose == PlayerPose.sleep),
    elytraFlying(ctx -> ctx.pose == PlayerPose.elytraFly),
    crawling(ctx -> ctx.pose == PlayerPose.crawl),
    ;

    private static final float PI = (float) Math.PI;
    private static final HashMap<String, ExprVariable> TABLE = new HashMap<>();

    static {
        for (ExprVariable f : values()) {
            if (f.name != null)
                TABLE.put(f.name, f);
        }
    }

    public final String name;
    private final Function<EvalContext, Object> func;

    ExprVariable(Function<EvalContext, Object> func) {
        this.name = name();
        this.func = func;
    }

    ExprVariable(String name, Function<EvalContext, Object> func) {
        this.name = name;
        this.func = func;
    }

    public static ExprVariable find(String name) {
        return TABLE.get(name);
    }

    @Override
    public boolean asBool(EvalContext ctx) {
        return (boolean) func.apply(ctx);
    }

    @Override
    public float asFloat(EvalContext ctx) {
        return (float) func.apply(ctx);
    }
}
