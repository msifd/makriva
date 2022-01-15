package msifeed.makriva.model;

import msifeed.makriva.expr.ConstBool;
import msifeed.makriva.expr.IEvalContext;
import msifeed.makriva.expr.IExpr;

import java.util.*;
import java.util.function.Consumer;

public class AnimationRules {
    public IExpr condition = new ConstBool(true);
    public List<AnimationRules> children = new ArrayList<>();

    public Map<BipedPart, IExpr[]> skeleton = new EnumMap<>(BipedPart.class);
    public Map<String, BoneParams> bones = new HashMap<>();

    public void acceptActive(IEvalContext ctx, Consumer<AnimationRules> visitor) {
        if (ctx.bool(condition)) {
            visitor.accept(this);
            for (AnimationRules child : children)
                child.acceptActive(ctx, visitor);
        }
    }

    public static class BoneParams {
        public IExpr[] rotation = null;
        public IExpr visible = null;
    }
}
