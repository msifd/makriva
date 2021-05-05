package msifeed.makriva.expr;

public interface IExpr {
    boolean asBool(EvalContext ctx);

    float asFloat(EvalContext ctx);
}
