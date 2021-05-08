package msifeed.makriva.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.BipedPart;
import msifeed.makriva.expr.IExpr;
import msifeed.makriva.expr.context.EvalContext;
import msifeed.makriva.mixins.render.ModelPlayerGetterMixin;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import java.util.Map;

public class StatureHandler {
    public static void setSkeletonOffsets(ModelPlayer biped, Entity entity, float scale) {
        final ModelShape model = Makriva.MODELS.getModelWithoutBuild(entity.getUniqueID());
        if (model == null) return;

        final EvalContext ctx = model.context;

        for (Map.Entry<BipedPart, IExpr[]> e : model.shape.skeleton.entrySet()) {
            final ModelRenderer[] parts = e.getKey().findParts(biped);
            if (parts == null) continue;

            final IExpr[] off = e.getValue();
            for (ModelRenderer part : parts) {
                part.offsetX = ctx.num(off[0]) * scale;
                part.offsetY = ctx.num(off[1]) * scale;
                part.offsetZ = ctx.num(off[2]) * scale;
            }
        }
    }

    public static void resetSkeletonOffsets(ModelPlayer model) {
        for (BipedPart bp : BipedPart.values()) {
            final ModelRenderer[] parts = bp.findParts(model);
            if (parts == null) continue;

            for (ModelRenderer part : parts) {
                part.offsetX = 0;
                part.offsetY = 0;
                part.offsetZ = 0;
            }
        }
    }

    public static void setRotationPoints(ModelPlayer biped, Entity entity, float scale) {
        final ModelShape model = Makriva.MODELS.getModelWithoutBuild(entity.getUniqueID());
        if (model == null) return;

        final EvalContext ctx = model.context;

        for (Map.Entry<BipedPart, IExpr[]> e : model.shape.skeleton.entrySet()) {
            final ModelRenderer[] parts = e.getKey().findParts(biped);
            if (parts == null) continue;

            final IExpr[] off = e.getValue();
            for (ModelRenderer part : parts) {
                part.rotationPointX += ctx.num(off[0]) * scale;
                part.rotationPointY += ctx.num(off[1]) * scale;
                part.rotationPointZ += ctx.num(off[2]) * scale;
            }
        }
    }

    public static void resetRotationPoints(ModelPlayer model) {
        final ModelPlayerGetterMixin getter = (ModelPlayerGetterMixin) model;

        model.bipedHead.setRotationPoint(0, 0, 0);
        model.bipedHeadwear.setRotationPoint(0, 0, 0);
        model.bipedBody.setRotationPoint(0, 0, 0);
        model.bipedBodyWear.setRotationPoint(0, 0, 0);

        if (getter.isSmallArms()) {
            model.bipedLeftArm.setRotationPoint(5, 2.5F, 0);
            model.bipedLeftArmwear.setRotationPoint(5, 2.5F, 0);
            model.bipedRightArm.setRotationPoint(-5, 2.5F, 0);
            model.bipedRightArmwear.setRotationPoint(-5, 2.5F, 10);
        } else {
            model.bipedLeftArm.setRotationPoint(5, 2, 0);
            model.bipedLeftArmwear.setRotationPoint(5, 2, 0);
            model.bipedRightArm.setRotationPoint(-5, 2, 0);
            model.bipedRightArmwear.setRotationPoint(-5, 2, 10);
        }

        model.bipedLeftLeg.setRotationPoint(1.9f, 12, 0);
        model.bipedLeftLegwear.setRotationPoint(1.9f, 12, 0);
        model.bipedRightLeg.setRotationPoint(-1.9f, 12, 0);
        model.bipedRightLegwear.setRotationPoint(-1.9f, 12, 0);
    }
}
