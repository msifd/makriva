package msifeed.makriva.expr;

import msifeed.makriva.expr.context.EvalContext;

public class ConstFloat implements IExpr {
    public final float value;

    public ConstFloat(float value) {
        this.value = value;
    }

    @Override
    public boolean asBool(EvalContext ctx) {
        return value > 0;
    }

    @Override
    public float asFloat(EvalContext ctx) {
        return value;
    }

    @Override
    public String toString() {
        return "ConstFloat{" + value + '}';
    }
}
