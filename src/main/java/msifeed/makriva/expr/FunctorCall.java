package msifeed.makriva.expr;

public class FunctorCall implements IExpr {
    public final ExprFunctor func;
    public final IExpr[] args;

    public FunctorCall(ExprFunctor func) {
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
