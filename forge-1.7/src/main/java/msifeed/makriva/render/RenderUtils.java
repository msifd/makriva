package msifeed.makriva.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.MakrivaCommons;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.BipedPart;
import msifeed.makriva.model.PlayerPose;
import msifeed.makriva.model.Shape;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

import java.util.UUID;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class RenderUtils {
    public static void renderWithExternalTransform(ModelRenderer model, float scale, Consumer<Float> render) {
        GL11.glTranslated(model.offsetX, model.offsetY, model.offsetZ);

        if (model.rotateAngleX == 0 && model.rotateAngleY == 0 && model.rotateAngleZ == 0) {
            if (model.rotationPointX == 0 && model.rotationPointY == 0 && model.rotationPointZ == 0) {
                render.accept(scale);
            } else {
                GL11.glTranslated(model.rotationPointX * scale, model.rotationPointY * scale, model.rotationPointZ * scale);
                render.accept(scale);
                GL11.glTranslated(-model.rotationPointX * scale, -model.rotationPointY * scale, -model.rotationPointZ * scale);
            }
        } else {
            GL11.glPushMatrix();
            GL11.glTranslated(model.rotationPointX * scale, model.rotationPointY * scale, model.rotationPointZ * scale);
            if (model.rotateAngleZ != 0) GL11.glRotated(model.rotateAngleZ * (180f / (float) Math.PI), 0, 0, 1);
            if (model.rotateAngleY != 0) GL11.glRotated(model.rotateAngleY * (180f / (float) Math.PI), 0, 1, 0);
            if (model.rotateAngleX != 0) GL11.glRotated(model.rotateAngleX * (180f / (float) Math.PI), 1, 0, 0);
            render.accept(scale);
            GL11.glPopMatrix();
        }

        GL11.glTranslated(-model.offsetX, -model.offsetY, -model.offsetZ);
    }

    public static void externalTransform(ModelRenderer model, float scale) {
        if (model.rotateAngleX == 0 && model.rotateAngleY == 0 && model.rotateAngleZ == 0) {
            if (model.rotationPointX != 0 || model.rotationPointY != 0 || model.rotationPointZ != 0) {
                GL11.glTranslated(model.rotationPointX * scale, model.rotationPointY * scale, model.rotationPointZ * scale);
            }
        } else {
            GL11.glTranslated(model.rotationPointX * scale, model.rotationPointY * scale, model.rotationPointZ * scale);
            if (model.rotateAngleZ != 0) GL11.glRotated(model.rotateAngleZ * (180f / (float) Math.PI), 0, 0, 1);
            if (model.rotateAngleY != 0) GL11.glRotated(model.rotateAngleY * (180f / (float) Math.PI), 0, 1, 0);
            if (model.rotateAngleX != 0) GL11.glRotated(model.rotateAngleX * (180f / (float) Math.PI), 1, 0, 0);
        }
    }

    public static void setPlayerSkeletonOffsets(ModelBiped biped, AbstractClientPlayer entity, float scale) {
        final UUID uuid = entity.getGameProfile().getId();
        final ModelShape model = RenderBridge.getModelWithoutBuild(uuid);
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
        final ModelShape model = RenderBridge.getModelWithoutBuild(entity.getUniqueID());
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
        final Shape shape = MakrivaShared.MODELS.getShape(player.getGameProfile().getId());
        if (shape.modelScale != 1) {
            if (player.isSneaking())
                y += 0.125 - 0.125 * shape.modelScale;
            if (MakrivaCommons.findPose(player) == PlayerPose.sit)
                y += 0.5 * (1 - shape.modelScale);
        }
        return y;
    }

    public static void updateEvalPlayer(AbstractClientPlayer player) {
        final RenderContext ctx = (RenderContext) SharedRenderState.EVAL_CTX;
        ctx.player = player;
        ctx.sneaking = player.isSneaking();
        ctx.currentPose = MakrivaCommons.findPose(player);
        ctx.limbSwing = player.limbSwingAmount;
        ctx.limbSwingTicks = player.limbSwing;
        ctx.ageInTicks = player.getAge();
        ctx.netHeadYaw = player.getRotationYawHead();
        ctx.headPitch = player.rotationPitch;
    }

    public static void updateEvalPartialTicks(float partialTicks) {
        final RenderContext ctx = (RenderContext) SharedRenderState.EVAL_CTX;
        ctx.partialTicks = partialTicks;
    }

    public static void updateEvalScale(float scale) {
        final RenderContext ctx = (RenderContext) SharedRenderState.EVAL_CTX;
        ctx.scale = scale;
    }
}
