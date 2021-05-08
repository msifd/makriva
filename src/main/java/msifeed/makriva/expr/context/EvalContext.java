package msifeed.makriva.expr.context;

import msifeed.makriva.expr.IExpr;
import net.minecraft.client.entity.AbstractClientPlayer;

public class EvalContext {
    public RenderParams renderParams = new RenderParams();
    public AbstractClientPlayer player = null;

    public boolean bool(IExpr expr) {
        return expr != null && expr.asBool(this);
    }

    public float num(IExpr expr) {
        return expr != null ? expr.asFloat(this) : 0;
    }
}
