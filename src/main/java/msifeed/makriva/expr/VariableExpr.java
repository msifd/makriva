package msifeed.makriva.expr;

public class VariableExpr implements IExpr {
    private final String name;

    public VariableExpr(String name) {
        this.name = name;
    }

    @Override
    public boolean asBool(EvalContext ctx) {
        return false;
    }

    @Override
    public float asFloat(EvalContext ctx) {
        return 0;
    }
}
