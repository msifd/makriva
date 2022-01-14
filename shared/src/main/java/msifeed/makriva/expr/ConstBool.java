package msifeed.makriva.expr;

public class ConstBool implements IExpr {
    public final boolean value;

    public ConstBool(boolean value) {
        this.value = value;
    }

    @Override
    public boolean asBool(IEvalContext ctx) {
        return value;
    }

    @Override
    public float asFloat(IEvalContext ctx) {
        return value ? 1f : 0f;
    }

    @Override
    public String toString() {
        return "ConstBool{" + value + '}';
    }
}
