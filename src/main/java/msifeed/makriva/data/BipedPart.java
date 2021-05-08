package msifeed.makriva.data;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public enum BipedPart {
    head, body, right_arm, left_arm, right_leg, left_leg;

    @SideOnly(Side.CLIENT)
    @Nullable
    public ModelRenderer findPart(ModelPlayer model) {
        switch (this) {
            case head:
                return model.bipedHead;
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
            default:
                return null;
        }
    }

    @SideOnly(Side.CLIENT)
    @Nullable
    public ModelRenderer[] findParts(ModelPlayer model) {
        switch (this) {
            case head:
                return new ModelRenderer[]{model.bipedHead, model.bipedHeadwear};
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
            default:
                return null;
        }
    }
}
