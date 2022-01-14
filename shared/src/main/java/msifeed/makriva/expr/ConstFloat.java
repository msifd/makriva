package msifeed.makriva.expr;

public class ConstFloat implements IExpr {
    public final float value;

    public ConstFloat(float value) {
        this.value = value;
    }

    @Override
    public boolean asBool(IEvalContext ctx) {
        return value > 0;
    }

    @Override
    public float asFloat(IEvalContext ctx) {
        return value;
    }

    @Override
    public String toString() {
        return "ConstFloat{" + value + '}';
    }
}
