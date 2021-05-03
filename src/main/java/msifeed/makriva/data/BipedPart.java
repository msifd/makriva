package msifeed.makriva.data;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public enum BipedPart {
    head, body, right_arm, left_arm, right_leg, left_leg;

    @SideOnly(Side.CLIENT)
    @Nullable
    public ModelRenderer findPart(ModelBiped biped) {
        switch (this) {
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
}
