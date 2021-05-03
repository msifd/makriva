package msifeed.makriva.render.model;

import msifeed.makriva.data.Bone;
import msifeed.makriva.data.Box;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class ModelBone extends ModelRenderer {
    public final ModelShape base;
    public final Bone spec;
    public final ModelRenderer parent;

    public ModelBone(ModelShape base, Bone spec, ModelRenderer parent) {
        super(base, spec.id);
        this.base = base;
        this.spec = spec;
        this.parent = parent;

        setRotationPoint(spec.rotationPoint[0], spec.rotationPoint[1], spec.rotationPoint[2]);

        for (Box box : spec.boxes) {
            cubeList.add(new ModelBox(this,
                    box.uv[0], box.uv[1],
                    box.pos[0], box.pos[1], box.pos[2],
                    box.size[0], box.size[1], box.size[2],
                    box.delta, box.mirrored));
        }

        for (Bone b : spec.children) {
            addChild(new ModelBone(base, b, null));
        }
    }

    @Override
    public void render(float scale) {
        this.offsetX = spec.offset[0] * scale;
        this.offsetY = spec.offset[1] * scale;
        this.offsetZ = spec.offset[2] * scale;

        this.rotationPointX += spec.rotationPoint[0];
        this.rotationPointY += spec.rotationPoint[1];
        this.rotationPointZ += spec.rotationPoint[2];
        this.rotateAngleX += spec.rotation[0];
        this.rotateAngleY += spec.rotation[1];
        this.rotateAngleZ += spec.rotation[2];

        if (parent != null) {
            renderWithParentTransform(scale);
        } else {
            super.render(scale);
        }

        setRotationPoint(0, 0, 0);
        this.rotateAngleX = 0;
        this.rotateAngleY = 0;
        this.rotateAngleZ = 0;
    }

    private void renderWithParentTransform(float scale) {
        GlStateManager.translate(parent.offsetX, parent.offsetY, parent.offsetZ);

        if (parent.rotateAngleX == 0 && parent.rotateAngleY == 0 && parent.rotateAngleZ == 0) {
            if (parent.rotationPointX == 0 && parent.rotationPointY == 0 && parent.rotationPointZ == 0) {

                super.render(scale);
            } else {
                GlStateManager.translate(parent.rotationPointX * scale, parent.rotationPointY * scale, parent.rotationPointZ * scale);

                super.render(scale);

                GlStateManager.translate(-parent.rotationPointX * scale, -parent.rotationPointY * scale, -parent.rotationPointZ * scale);
            }
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate(parent.rotationPointX * scale, parent.rotationPointY * scale, parent.rotationPointZ * scale);

            if (parent.rotateAngleZ != 0) {
                GlStateManager.rotate(parent.rotateAngleZ * (180f / (float) Math.PI), 0, 0, 1);
            }

            if (parent.rotateAngleY != 0) {
                GlStateManager.rotate(parent.rotateAngleY * (180f / (float) Math.PI), 0, 1, 0);
            }

            if (parent.rotateAngleX != 0) {
                GlStateManager.rotate(parent.rotateAngleX * (180f / (float) Math.PI), 1, 0, 0);
            }

            super.render(scale);

            GlStateManager.popMatrix();
        }

        GlStateManager.translate(-parent.offsetX, -parent.offsetY, -parent.offsetZ);
    }

}
