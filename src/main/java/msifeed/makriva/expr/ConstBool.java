package msifeed.makriva.expr;

import msifeed.makriva.expr.context.EvalContext;

public class ConstBool implements IExpr {
    public final boolean value;

    public ConstBool(boolean value) {
        this.value = value;
    }

    @Override
    public boolean asBool(EvalContext ctx) {
        return value;
    }

    @Override
    public float asFloat(EvalContext ctx) {
        return value ? 1f : 0f;
    }

    @Override
    public String toString() {
        return "ConstBool{" + value + '}';
    }
}
