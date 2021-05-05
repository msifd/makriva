package msifeed.makriva.expr;

import msifeed.makriva.expr.context.EvalContext;

import java.util.HashMap;
import java.util.function.Function;

public enum ExprVariable implements IExpr {
    limbSwing("limbSwing", ctx -> ctx.renderParams.limbSwing),
    limbSwingAmount("limbSwingAmount", ctx -> ctx.renderParams.limbSwingAmount),
    ;

    private static final HashMap<String, ExprVariable> TABLE = new HashMap<>();

    static {
        for (ExprVariable f : values()) {
            if (f.name != null)
                TABLE.put(f.name, f);
        }
    }

    public final String name;
    private final Function<EvalContext, Object> func;

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
