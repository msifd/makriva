package msifeed.makriva.render.model;

import msifeed.makriva.data.Bone;
import msifeed.makriva.data.Cube;
import msifeed.makriva.data.Quad;
import msifeed.makriva.expr.context.EvalContext;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModelBone extends ModelRenderer {
    public final ModelShape base;
    public final Bone spec;
    public final ModelRenderer parent;

    private final List<ModelCube> cubes = new ArrayList<>();
    private final List<ModelQuad> quads = new ArrayList<>();

    private boolean compiled = false;
    private int displayList = 0;

    public ModelBone(ModelShape base, Bone spec, ModelRenderer parent) {
        super(base, spec.id);
        setTextureSize(spec.textureSize[0], spec.textureSize[1]);

        this.base = base;
        this.spec = spec;
        this.parent = parent;

        setRotationPoint(spec.rotationPoint[0], spec.rotationPoint[1], spec.rotationPoint[2]);

        for (Cube cube : spec.cubes) cubes.add(new ModelCube(this, cube));
        for (Quad quad : spec.quads) quads.add(new ModelQuad(this, quad));
        for (Bone bone : spec.bones) addChild(new ModelBone(base, bone, null));
    }

    @Override
    public void render(float scale) {
        if (isHidden || !showModel) return;
        if (!compiled) compileDisplayList(scale);

        final EvalContext ctx = base.context;

        this.offsetX = spec.offset[0] * scale;
        this.offsetY = spec.offset[1] * scale;
        this.offsetZ = spec.offset[2] * scale;

        this.rotationPointX += spec.rotationPoint[0];
        this.rotationPointY += spec.rotationPoint[1];
        this.rotationPointZ += spec.rotationPoint[2];
        this.rotateAngleX += ctx.num(spec.rotation[0]);
        this.rotateAngleY += ctx.num(spec.rotation[1]);
        this.rotateAngleZ += ctx.num(spec.rotation[2]);

        if (parent != null) {
            withModelTransform(parent, scale, sc -> {
                withModelTransform(this, scale, this::renderSelf);
            });
        } else {
            withModelTransform(this, scale, this::renderSelf);
        }

        setRotationPoint(0, 0, 0);
        this.rotateAngleX = 0;
        this.rotateAngleY = 0;
        this.rotateAngleZ = 0;
    }

    private void renderSelf(float scale) {
        GlStateManager.callList(displayList);

        if (childModels != null) {
            for (ModelRenderer child : childModels)
                child.render(scale);
        }
    }

    private void withModelTransform(ModelRenderer model, float scale, Consumer<Float> render) {
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

    private void compileDisplayList(float scale) {
        displayList = GLAllocation.generateDisplayLists(1);
        GlStateManager.glNewList(displayList, 4864);
        BufferBuilder buf = Tessellator.getInstance().getBuffer();

        for (ModelCube cube : cubes) cube.render(buf, scale);
        for (ModelQuad quad : quads) quad.render(buf, scale);

        GlStateManager.glEndList();
        compiled = true;
    }
}
