package msifeed.makriva.data;

import msifeed.makriva.expr.ConstBool;
import msifeed.makriva.expr.IExpr;
import msifeed.makriva.expr.context.EvalContext;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class AnimationRules {
    public IExpr condition = new ConstBool(true);
    public List<AnimationRules> children = new ArrayList<>();

    public Map<BipedPart, IExpr[]> skeleton = new EnumMap<>(BipedPart.class);
    public Map<String, BoneParams> bones = new HashMap<>();

    public Stream<AnimationRules> streamAll() {
        return Stream.concat(Stream.of(this), children.stream());
    }

    public void acceptActive(EvalContext ctx, Consumer<AnimationRules> visitor) {
        if (ctx.bool(condition)) {
            visitor.accept(this);
            for (AnimationRules child : children)
                child.acceptActive(ctx, visitor);
        }
    }

    public static class BoneParams {
        public IExpr[] rotation = new IExpr[3];
    }
}
