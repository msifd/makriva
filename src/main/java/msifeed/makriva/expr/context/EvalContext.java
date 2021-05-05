package msifeed.makriva.expr.context;

import msifeed.makriva.expr.IExpr;

public class EvalContext {
    public RenderParams renderParams = new RenderParams();

    public boolean bool(IExpr expr) {
        return expr != null && expr.asBool(this);
    }

    public float num(IExpr expr) {
        return expr != null ? expr.asFloat(this) : 0;
    }
}
