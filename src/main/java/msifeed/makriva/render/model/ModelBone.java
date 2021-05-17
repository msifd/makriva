package msifeed.makriva.render.model;

import msifeed.makriva.data.BipedPart;
import msifeed.makriva.data.Bone;
import msifeed.makriva.data.Cube;
import msifeed.makriva.data.Quad;
import msifeed.makriva.expr.context.EvalContext;
import msifeed.makriva.render.PartSelector;
import msifeed.makriva.render.RenderUtils;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;

import java.util.ArrayList;
import java.util.List;

public class ModelBone extends ModelRenderer {
    public final ModelShape base;
    public final Bone spec;

    private final List<ModelCube> cubes = new ArrayList<>();
    private final List<ModelQuad> quads = new ArrayList<>();

    private boolean compiled = false;
    private int displayList = 0;

    public ModelBone(ModelShape base, Bone spec) {
        super(base, spec.id);
        setTextureSize(spec.textureSize[0], spec.textureSize[1]);

        this.base = base;
        this.spec = spec;

        setRotationPoint(spec.rotationPoint[0], spec.rotationPoint[1], spec.rotationPoint[2]);

        for (Cube cube : spec.cubes) cubes.add(new ModelCube(this, cube));
        for (Quad quad : spec.quads) quads.add(new ModelQuad(this, quad));
        for (Bone bone : spec.bones) addChild(new ModelBone(base, bone));
    }

    @Override
    public void render(float scale) {
        if (isHidden || !showModel) return;
        if (!compiled) compileDisplayList(scale);

        final EvalContext ctx = base.context;

        this.offsetX = spec.offset[0] * scale;
        this.offsetY = spec.offset[1] * scale;
        this.offsetZ = spec.offset[2] * scale;

        this.rotationPointX = spec.rotationPoint[0];
        this.rotationPointY = spec.rotationPoint[1];
        this.rotationPointZ = spec.rotationPoint[2];

        final float D2R = (float) (180 / Math.PI);
        this.rotateAngleX = ctx.num(spec.rotation[0]) / D2R;
        this.rotateAngleY = ctx.num(spec.rotation[1]) / D2R;
        this.rotateAngleZ = ctx.num(spec.rotation[2]) / D2R;

        if (spec.parent != null) {
            final ModelRenderer parent = PartSelector.findPart(base.render.getMainModel(), spec.parent);
            if (parent.rotationPointY <= 9) {
                this.rotateAngleY += 0;
            }

            RenderUtils.renderWithExternalTransform(parent, scale, sc -> {
                RenderUtils.renderWithExternalTransform(this, sc, this::renderSelf);
            });
        } else {
            RenderUtils.renderWithExternalTransform(this, scale, this::renderSelf);
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
