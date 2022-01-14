package msifeed.makriva.expr;

public interface IExpr {
    boolean asBool(IEvalContext ctx);

    float asFloat(IEvalContext ctx);
}
