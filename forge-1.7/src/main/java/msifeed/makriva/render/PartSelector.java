package msifeed.makriva.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.model.BipedPart;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class PartSelector {
    public static ModelRenderer findPart(ModelBiped model, BipedPart part) {
        switch (part) {
            case head:
                return model.bipedHead;
            default:
            case body:
                return model.bipedBody;
            case right_arm:
                return model.bipedRightArm;
            case left_arm:
                return model.bipedLeftArm;
            case right_leg:
                return model.bipedRightLeg;
            case left_leg:
                return model.bipedLeftLeg;
        }
    }

    public static ModelRenderer[] findParts(ModelBiped model, BipedPart part) {
        switch (part) {
            case head:
                return new ModelRenderer[]{model.bipedHead, model.bipedHeadwear};
            default:
            case body:
                return new ModelRenderer[]{model.bipedBody};
            case right_arm:
                return new ModelRenderer[]{model.bipedRightArm};
            case left_arm:
                return new ModelRenderer[]{model.bipedLeftArm};
            case right_leg:
                return new ModelRenderer[]{model.bipedRightLeg};
            case left_leg:
                return new ModelRenderer[]{model.bipedLeftLeg};
        }
    }
}
