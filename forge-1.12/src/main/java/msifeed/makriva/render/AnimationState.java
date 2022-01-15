package msifeed.makriva.render;

import msifeed.makriva.expr.IExpr;
import msifeed.makriva.model.AnimationRules;
import msifeed.makriva.model.BipedPart;

import java.util.*;

public class AnimationState {
    private static final float[] ZEROS = new float[]{0, 0, 0};

    public final Map<BipedPart, float[]> skeleton = new EnumMap<>(BipedPart.class);
    public final Map<String, float[]> rotations = new HashMap<>();
    public final Set<String> hidden = new HashSet<>();
    private final AnimationRules rules;

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

    public void update() {
        skeleton.clear();
        rotations.clear();
        hidden.clear();
        rules.acceptActive(RenderContext.CTX, this::visitRule);
    }

    private void visitRule(AnimationRules rule) {
        for (Map.Entry<BipedPart, IExpr[]> entry : rule.skeleton.entrySet()) {
            final float[] offset = skeleton.computeIfAbsent(entry.getKey(), s -> new float[3]);
            merge3(offset, entry.getValue());
        }

        for (Map.Entry<String, AnimationRules.BoneParams> entry : rule.bones.entrySet()) {
            final AnimationRules.BoneParams params = entry.getValue();
            if (params.rotation != null) {
                final float[] rots = rotations.computeIfAbsent(entry.getKey(), s -> new float[3]);
                merge3(rots, params.rotation);
            }
            if (params.visible != null) {
                if (!RenderContext.CTX.bool(params.visible))
                    hidden.add(entry.getKey());
            }
        }
    }

    private void merge3(float[] values, IExpr[] expr) {
        values[0] += RenderContext.CTX.num(expr[0]);
        values[1] += RenderContext.CTX.num(expr[1]);
        values[2] += RenderContext.CTX.num(expr[2]);
    }
}
