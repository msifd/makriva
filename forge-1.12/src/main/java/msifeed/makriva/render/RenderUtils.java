package msifeed.makriva.render;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

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
}
