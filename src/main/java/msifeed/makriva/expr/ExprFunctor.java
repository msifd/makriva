package msifeed.makriva.expr;

import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.function.BiFunction;

public enum ExprFunctor {
    condition("if", 3, (ctx, args) -> ctx.bool(args[0]) ? ctx.num(args[1]) : ctx.num(args[2])),

    plus("+", 2, (ctx, args) -> ctx.num(args[0]) + ctx.num(args[1])),
    minus("-", 2, (ctx, args) -> ctx.num(args[0]) - ctx.num(args[1])),
    multiply("*", 2, (ctx, args) -> ctx.num(args[0]) * ctx.num(args[1])),
    divide("/", 2, (ctx, args) -> ctx.num(args[0]) / ctx.num(args[1])),
    negate(1, (ctx, args) -> -ctx.num(args[0])),

    sin(1, (ctx, args) -> MathHelper.sin(ctx.num(args[0]))),
    cos(1, (ctx, args) -> MathHelper.cos(ctx.num(args[0]))),
    sqrt(1, (ctx, args) -> MathHelper.sqrt(ctx.num(args[0]))),
    floor(1, (ctx, args) -> MathHelper.floor(ctx.num(args[0]))),
    ceil(1, (ctx, args) -> MathHelper.ceil(ctx.num(args[0]))),
    clamp(3, (ctx, args) -> MathHelper.clamp(ctx.num(args[0]), ctx.num(args[1]), ctx.num(args[2]))),
    min(2, (ctx, args) -> Math.min(ctx.num(args[0]), ctx.num(args[1]))),
    max(2, (ctx, args) -> Math.max(ctx.num(args[0]), ctx.num(args[1]))),

    random(0, (ctx, args) -> (float) Math.random());

    public final String name;
    public final int args;
    private final BiFunction<EvalContext, IExpr[], Object> func;

    ExprFunctor(int args, BiFunction<EvalContext, IExpr[], Object> func) {
        this.name = name().toLowerCase();
        this.args = args;
        this.func = func;
    }

    ExprFunctor(String name, int args, BiFunction<EvalContext, IExpr[], Object> func) {
        this.name = name;
        this.args = args;
        this.func = func;
    }

    public Object eval(EvalContext ctx, IExpr[] args) {
        return func.apply(ctx, args);
    }

    private static final HashMap<String, ExprFunctor> TABLE = new HashMap<>();

    public static ExprFunctor find(String name) {
        return TABLE.get(name);
    }

    static {
        for (ExprFunctor f : values()) {
            TABLE.put(f.name, f);
        }
    }
}
