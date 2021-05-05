package msifeed.makriva.expr;

import msifeed.makriva.expr.context.EvalContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.function.BiFunction;

public enum ExprFunction {
    brackets(null, 1, 0, null),
    condition("if", 1, 3, (ctx, args) -> ctx.bool(args[0]) ? ctx.num(args[1]) : ctx.num(args[2])),

    not("!", 2, 1, (ctx, args) -> !ctx.bool(args[0])),
    and("&&", 11, 2, (ctx, args) -> ctx.bool(args[0]) && ctx.bool(args[1])),
    or("||", 12, 2, (ctx, args) -> ctx.bool(args[0]) || ctx.bool(args[1])),

    eq("==", 7, 2, (ctx, args) -> ctx.num(args[0]) == ctx.num(args[1])),
    neq("!=", 7, 2, (ctx, args) -> ctx.num(args[0]) != ctx.num(args[1])),
    lt("<", 6, 2, (ctx, args) -> ctx.num(args[0]) < ctx.num(args[1])),
    let("<=", 6, 2, (ctx, args) -> ctx.num(args[0]) <= ctx.num(args[1])),
    gt(">", 6, 2, (ctx, args) -> ctx.num(args[0]) > ctx.num(args[1])),
    get(">=", 6, 2, (ctx, args) -> ctx.num(args[0]) >= ctx.num(args[1])),

    plus("+", 4, 2, (ctx, args) -> ctx.num(args[0]) + ctx.num(args[1])),
    minus("-", 4, 2, (ctx, args) -> ctx.num(args[0]) - ctx.num(args[1])),
    multiply("*", 3, 2, (ctx, args) -> ctx.num(args[0]) * ctx.num(args[1])),
    divide("/", 3, 2, (ctx, args) -> ctx.num(args[0]) / ctx.num(args[1])),
    modulus("%", 3, 2, (ctx, args) -> ctx.num(args[0]) % ctx.num(args[1])),
    negate("negate", 2, 1, (ctx, args) -> -ctx.num(args[0])),

    sin(1, (ctx, args) -> MathHelper.sin(ctx.num(args[0]))),
    cos(1, (ctx, args) -> MathHelper.cos(ctx.num(args[0]))),
    sqrt(1, (ctx, args) -> MathHelper.sqrt(ctx.num(args[0]))),
    floor(1, (ctx, args) -> (float) MathHelper.floor(ctx.num(args[0]))),
    ceil(1, (ctx, args) -> (float) MathHelper.ceil(ctx.num(args[0]))),
    clamp(3, (ctx, args) -> MathHelper.clamp(ctx.num(args[0]), ctx.num(args[1]), ctx.num(args[2]))),
    min(2, (ctx, args) -> Math.min(ctx.num(args[0]), ctx.num(args[1]))),
    max(2, (ctx, args) -> Math.max(ctx.num(args[0]), ctx.num(args[1]))),

    random(0, (ctx, args) -> (float) Math.random()),
    time(0, (ctx, args) -> {
        final Minecraft mc = Minecraft.getMinecraft();
        final WorldClient world = mc.world;
        return world != null
                ? (world.getTotalWorldTime() % 24000) + mc.getRenderPartialTicks()
                : 0f;
    }),
    ;

    private static final HashMap<String, ExprFunction> TABLE = new HashMap<>();

    static {
        for (ExprFunction f : values()) {
            if (f.name != null)
                TABLE.put(f.name, f);
        }
    }

    public final int precedence;
    public final String name;
    public final int args;
    private final BiFunction<EvalContext, IExpr[], Object> func;

    ExprFunction(int args, BiFunction<EvalContext, IExpr[], Object> func) {
        this.precedence = 1;
        this.name = name().toLowerCase();
        this.args = args;
        this.func = func;
    }

    ExprFunction(String name, int precedence, int args, BiFunction<EvalContext, IExpr[], Object> func) {
        this.precedence = precedence;
        this.name = name;
        this.args = args;
        this.func = func;
    }

    public static ExprFunction find(String name) {
        return TABLE.get(name);
    }

    public boolean precedes(ExprFunction func) {
        return this.precedence < func.precedence;
    }

    public Object eval(EvalContext ctx, IExpr[] args) {
        return func.apply(ctx, args);
    }
}
