package msifeed.makriva.expr.context;

import msifeed.makriva.data.PlayerPose;
import msifeed.makriva.expr.IExpr;
import net.minecraft.client.entity.AbstractClientPlayer;

public class EvalContext {
    public RenderParams renderParams = new RenderParams();
    public AbstractClientPlayer player = null;
    public PlayerPose pose = PlayerPose.stand;

    public void update(AbstractClientPlayer player) {
        this.player = player;
        this.pose = PlayerPose.get(player);
    }

    public boolean bool(IExpr expr) {
        try {
            return expr.asBool(this);
        } catch (Exception e) {
            return false;
        }
    }

    public float num(IExpr expr) {
        try {
            return expr.asFloat(this);
        } catch (Exception e) {
            return 0;
        }
    }
}
