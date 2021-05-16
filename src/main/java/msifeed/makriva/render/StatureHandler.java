package msifeed.makriva.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.BipedPart;
import msifeed.makriva.expr.IExpr;
import msifeed.makriva.expr.context.EvalContext;
import msifeed.makriva.mixins.render.ModelPlayerGetterMixin;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Map;
import java.util.UUID;

public class StatureHandler {
    public static void setPlayerSkeletonOffsets(ModelPlayer biped, EntityPlayer entity, float scale) {
        final UUID uuid = entity.getGameProfile().getId();
        final ModelShape model = Makriva.MODELS.getModelWithoutBuild(uuid);
        if (model == null) return;

        for (BipedPart bp : BipedPart.values()) {
            final ModelRenderer[] parts = PartSelector.findParts(biped, bp);
            if (parts == null) continue;

            final IExpr[] exprs = model.shape.skeleton.get(bp);
            if (exprs != null) {
                final EvalContext ctx = model.context;
                for (ModelRenderer part : parts) {
                    part.offsetX = ctx.num(exprs[0]) * scale;
                    part.offsetY = ctx.num(exprs[1]) * scale;
                    part.offsetZ = ctx.num(exprs[2]) * scale;
                }
            } else {
                for (ModelRenderer part : parts) {
                    part.offsetX = 0;
                    part.offsetY = 0;
                    part.offsetZ = 0;
                }
            }
        }
    }

    public static void setBipedSkeletonOffsets(ModelBiped biped, EntityPlayer entity, float scale) {
        final ModelShape model = Makriva.MODELS.getModelWithoutBuild(entity.getUniqueID());
        if (model == null) return;

        for (BipedPart bp : BipedPart.values()) {
            final ModelRenderer part = PartSelector.findPart(biped, bp);
            if (part == null) continue;

            final IExpr[] exprs = model.shape.skeleton.get(bp);
            if (exprs != null) {
                final EvalContext ctx = model.context;
                part.offsetX = ctx.num(exprs[0]) * scale;
                part.offsetY = ctx.num(exprs[1]) * scale;
                part.offsetZ = ctx.num(exprs[2]) * scale;
            } else {
                part.offsetX = 0;
                part.offsetY = 0;
                part.offsetZ = 0;
            }
        }
    }
}
