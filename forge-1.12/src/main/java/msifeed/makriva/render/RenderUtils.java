package msifeed.makriva.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.MakrivaCommons;
import msifeed.makriva.model.BipedPart;
import msifeed.makriva.model.PlayerPose;
import msifeed.makriva.model.Shape;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

import java.util.UUID;
import java.util.function.Consumer;

public class RenderUtils {
    public static void renderWithExternalTransform(ModelRenderer model, float scale, Consumer<Float> render) {
        GlStateManager.translate(model.offsetX, model.offsetY, model.offsetZ);

        if (model.rotateAngleX == 0 && model.rotateAngleY == 0 && model.rotateAngleZ == 0) {
            if (model.rotationPointX == 0 && model.rotationPointY == 0 && model.rotationPointZ == 0) {
                render.accept(scale);
            } else {
                GlStateManager.translate(model.rotationPointX * scale, model.rotationPointY * scale, model.rotationPointZ * scale);
                render.accept(scale);
                GlStateManager.translate(-model.rotationPointX * scale, -model.rotationPointY * scale, -model.rotationPointZ * scale);
            }
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate(model.rotationPointX * scale, model.rotationPointY * scale, model.rotationPointZ * scale);
            if (model.rotateAngleZ != 0) GlStateManager.rotate(model.rotateAngleZ * (180f / (float) Math.PI), 0, 0, 1);
            if (model.rotateAngleY != 0) GlStateManager.rotate(model.rotateAngleY * (180f / (float) Math.PI), 0, 1, 0);
            if (model.rotateAngleX != 0) GlStateManager.rotate(model.rotateAngleX * (180f / (float) Math.PI), 1, 0, 0);
            render.accept(scale);
            GlStateManager.popMatrix();
        }

        GlStateManager.translate(-model.offsetX, -model.offsetY, -model.offsetZ);
    }

    public static void externalTransform(ModelRenderer model, float scale) {
        if (model.rotateAngleX == 0 && model.rotateAngleY == 0 && model.rotateAngleZ == 0) {
            if (model.rotationPointX != 0 || model.rotationPointY != 0 || model.rotationPointZ != 0) {
                GlStateManager.translate(model.rotationPointX * scale, model.rotationPointY * scale, model.rotationPointZ * scale);
            }
        } else {
            GlStateManager.translate(model.rotationPointX * scale, model.rotationPointY * scale, model.rotationPointZ * scale);
            if (model.rotateAngleZ != 0) GlStateManager.rotate(model.rotateAngleZ * (180f / (float) Math.PI), 0, 0, 1);
            if (model.rotateAngleY != 0) GlStateManager.rotate(model.rotateAngleY * (180f / (float) Math.PI), 0, 1, 0);
            if (model.rotateAngleX != 0) GlStateManager.rotate(model.rotateAngleX * (180f / (float) Math.PI), 1, 0, 0);
        }
    }

    public static void setPlayerSkeletonOffsets(ModelPlayer biped, AbstractClientPlayer entity, float scale) {
        final UUID uuid = entity.getGameProfile().getId();
        final ModelShape model = Makriva.MODELS.getModelWithoutBuild(uuid);
        if (model == null) return;

        for (BipedPart bp : BipedPart.values()) {
            final ModelRenderer[] parts = PartSelector.findParts(biped, bp);
            final float[] offset = model.getSkeletonOffset(bp);
            for (ModelRenderer part : parts) {
                part.offsetX = offset[0] * scale;
                part.offsetY = offset[1] * scale;
                part.offsetZ = offset[2] * scale;
            }
        }
    }

    public static void setBipedSkeletonOffsets(ModelBiped biped, AbstractClientPlayer entity, float scale) {
        final ModelShape model = Makriva.MODELS.getModelWithoutBuild(entity.getUniqueID());
        if (model == null) return;

        for (BipedPart bp : BipedPart.values()) {
            final ModelRenderer part = PartSelector.findPart(biped, bp);
            final float[] offset = model.getSkeletonOffset(bp);
            part.offsetX = offset[0] * scale;
            part.offsetY = offset[1] * scale;
            part.offsetZ = offset[2] * scale;
        }
    }

    public static double adjustYPosOfScaledModel(AbstractClientPlayer player, double y) {
        final Shape shape = Makriva.MODELS.getShape(player.getGameProfile().getId());
        if (shape.modelScale != 1) {
            if (player.isSneaking())
                y += 0.125 - 0.125 * shape.modelScale;
            if (MakrivaCommons.findPose(player) == PlayerPose.sit)
                y += 0.5 * (1 - shape.modelScale);
        }
        return y;
    }
}
