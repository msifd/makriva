package msifeed.makriva.expr;

public class FunctorCall implements IExpr {
    public final ExprFunction func;
    public final IExpr[] args;

    public FunctorCall(ExprFunction func) {
        this.func = func;
        this.args = new IExpr[func.args];
    }

    @Override
    public boolean asBool(IEvalContext ctx) {
        return (boolean) func.eval(ctx, args);
    }

    @Override
    public float asFloat(IEvalContext ctx) {
        return (float) func.eval(ctx, args);
    }
}
