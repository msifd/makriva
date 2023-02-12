package msifeed.makriva.render.model;

import msifeed.makriva.model.Bone;
import msifeed.makriva.model.Cube;
import msifeed.makriva.model.Quad;
import msifeed.makriva.render.PartSelector;
import msifeed.makriva.render.RenderUtils;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ModelBone extends ModelRenderer {
    public final ModelShape base;
    public final Bone spec;

    private final List<ModelCube> cubes = new ArrayList<>();
    private final List<ModelQuad> quads = new ArrayList<>();

    private boolean compiled = false;
    private int displayList = 0;

    public ModelBone(ModelShape base, Bone spec) {
        super(base, spec.id);
        setTextureSize(base.shape.textureSize[0], base.shape.textureSize[1]);

        this.base = base;
        this.spec = spec;

        setRotationPoint(spec.rotationPoint[0], spec.rotationPoint[1], spec.rotationPoint[2]);

        for (Cube cube : spec.cubes) cubes.add(new ModelCube(this, cube));
        for (Quad quad : spec.quads) quads.add(new ModelQuad(this, quad));
        for (Bone bone : spec.bones) addChild(new ModelBone(base, bone));
    }

    @Override
    public void render(float scale) {
        isHidden = base.animationState.hidden.contains(boxName);

        if (isHidden || !showModel) return;
        if (!compiled) compileDisplayList(scale);

        this.offsetX = spec.offset[0] * scale;
        this.offsetY = spec.offset[1] * scale;
        this.offsetZ = spec.offset[2] * scale;

        this.rotationPointX = spec.rotationPoint[0];
        this.rotationPointY = spec.rotationPoint[1];
        this.rotationPointZ = spec.rotationPoint[2];

        final float D2R = (float) (180 / Math.PI);
        final float[] animRotations = base.animationState.getRotations(boxName);
        this.rotateAngleX = (spec.rotation[0] + animRotations[0]) / D2R;
        this.rotateAngleY = (spec.rotation[1] + animRotations[1]) / D2R;
        this.rotateAngleZ = (spec.rotation[2] + animRotations[2]) / D2R;

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
