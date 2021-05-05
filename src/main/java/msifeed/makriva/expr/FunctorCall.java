package msifeed.makriva.expr;

import msifeed.makriva.expr.context.EvalContext;

public class FunctorCall implements IExpr {
    public final ExprFunction func;
    public final IExpr[] args;

    public FunctorCall(ExprFunction func) {
        this.func = func;
        this.args = new IExpr[func.args];
    }

    @Override
    public boolean asBool(EvalContext ctx) {
        return (boolean) func.eval(ctx, args);
    }

    @Override
    public float asFloat(EvalContext ctx) {
        return (float) func.eval(ctx, args);
    }
}
