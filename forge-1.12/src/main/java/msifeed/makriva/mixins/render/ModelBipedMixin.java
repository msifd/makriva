package msifeed.makriva.mixins.render;

import msifeed.makriva.render.RenderUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
public abstract class ModelBipedMixin {
    @Shadow
    protected abstract ModelRenderer getArmForSide(EnumHandSide side);

    @Inject(method = "setRotationAngles", at = @At("RETURN"))
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof AbstractClientPlayer)) return;

        final ModelBiped model = (ModelBiped) (Object) this;
        if (model instanceof ModelPlayer)
            RenderUtils.setPlayerSkeletonOffsets((ModelPlayer) model, (AbstractClientPlayer) entity, scale);
        else
            RenderUtils.setBipedSkeletonOffsets(model, (AbstractClientPlayer) entity, scale);
    }
}
