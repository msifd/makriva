package msifeed.makriva.render;

import msifeed.makriva.model.AnimationRules;
import msifeed.makriva.model.BipedPart;
import msifeed.makriva.expr.IEvalContext;
import msifeed.makriva.expr.IExpr;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class AnimationState {
    private static final float[] ZEROS = new float[]{0, 0, 0};

    public final Map<BipedPart, float[]> skeleton = new EnumMap<>(BipedPart.class);
    public final Map<String, float[]> rotations = new HashMap<>();
    private final AnimationRules rules;

    private transient IEvalContext ctx;

    public AnimationState(AnimationRules rules) {
        this.rules = rules;
    }

    public float[] getSkeletonOffset(BipedPart part) {
        return skeleton.getOrDefault(part, ZEROS.clone());
    }

    public float[] getRotations(String boneId) {
        return rotations.getOrDefault(boneId, ZEROS.clone());
    }

    // //

    public void update(IEvalContext ctx) {
        skeleton.clear();
        rotations.clear();
        this.ctx = ctx;
        rules.acceptActive(ctx, this::visitRule);
    }

    private void visitRule(AnimationRules rule) {
        for (Map.Entry<BipedPart, IExpr[]> entry : rule.skeleton.entrySet()) {
            final float[] offset = skeleton.computeIfAbsent(entry.getKey(), s -> new float[3]);
            merge3(offset, entry.getValue());
        }

        for (Map.Entry<String, AnimationRules.BoneParams> entry : rule.bones.entrySet()) {
            final float[] rots = rotations.computeIfAbsent(entry.getKey(), s -> new float[3]);
            merge3(rots, entry.getValue().rotation);
        }
    }

    private void merge3(float[] values, IExpr[] expr) {
        values[0] += ctx.num(expr[0]);
        values[1] += ctx.num(expr[1]);
        values[2] += ctx.num(expr[2]);
    }
}
