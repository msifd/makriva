package msifeed.makriva.render.model;

import msifeed.makriva.data.BipedPart;
import msifeed.makriva.data.Bone;
import msifeed.makriva.data.Shape;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class ModelShape extends ModelBase {
    public final RenderPlayer render;
    public final Shape shape;

    public final List<ModelBone> bones = new ArrayList<>();

    public ModelShape(RenderPlayer render, Shape shape) {
        this.render = render;
        this.shape = shape;

        final ModelPlayer modelPlayer = render.getMainModel();

        // TODO: two-phase base model init
        for (Bone bone : shape.bones) {
            final ModelBone model = new ModelBone(this, bone, findBipedPart(modelPlayer, bone.parent));
            bones.add(model);
        }
    }

    private static ModelRenderer findBipedPart(ModelBiped biped, BipedPart part) {
        switch (part) {
            case head:
                return biped.bipedHead;
            case body:
                return biped.bipedBody;
            case right_arm:
                return biped.bipedRightArm;
            case left_arm:
                return biped.bipedLeftArm;
            case right_leg:
                return biped.bipedRightLeg;
            case left_leg:
                return biped.bipedLeftLeg;
            default:
                return null;
        }
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entity.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }

        for (ModelBone bone : bones) {
            bone.render(scale);
        }
    }
}
