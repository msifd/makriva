package msifeed.makriva.render;

import msifeed.makriva.data.BipedPart;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    public static ModelRenderer[] findParts(ModelPlayer model, BipedPart part) {
        switch (part) {
            case head:
                return new ModelRenderer[]{model.bipedHead, model.bipedHeadwear};
            default:
            case body:
                return new ModelRenderer[]{model.bipedBody, model.bipedBodyWear};
            case right_arm:
                return new ModelRenderer[]{model.bipedRightArm, model.bipedRightArmwear};
            case left_arm:
                return new ModelRenderer[]{model.bipedLeftArm, model.bipedLeftArmwear};
            case right_leg:
                return new ModelRenderer[]{model.bipedRightLeg, model.bipedRightLegwear};
            case left_leg:
                return new ModelRenderer[]{model.bipedLeftLeg, model.bipedLeftLegwear};
        }
    }
}
