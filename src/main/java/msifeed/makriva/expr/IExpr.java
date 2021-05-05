package msifeed.makriva.expr;

import msifeed.makriva.expr.context.EvalContext;

public interface IExpr {
    boolean asBool(EvalContext ctx);

    float asFloat(EvalContext ctx);
}
