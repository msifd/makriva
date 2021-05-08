package msifeed.makriva.mixins.render;

import msifeed.makriva.render.StatureHandler;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPlayer.class)
public class ModelPlayerMixin {
    @Inject(method = "setRotationAngles", at = @At("RETURN"))
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, Entity entityIn, CallbackInfo ci) {
        final ModelPlayer model = (ModelPlayer) (Object) this;
        StatureHandler.setSkeletonOffsets(model, entityIn, scale);
    }
}
