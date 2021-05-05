package msifeed.makriva.expr;

public class EvalContext {

    public boolean bool(IExpr expr) {
        return expr != null && expr.asBool(this);
    }

    public float num(IExpr expr) {
        return expr != null ? expr.asFloat(this) : 0;
    }
}
