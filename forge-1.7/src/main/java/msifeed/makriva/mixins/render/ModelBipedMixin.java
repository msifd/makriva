package msifeed.makriva.mixins.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.render.RenderHandler;
import msifeed.makriva.render.RenderUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SideOnly(Side.CLIENT)
@Mixin(ModelBiped.class)
public class ModelBipedMixin {
    @Inject(method = "render", at = @At("RETURN"))
    public void render(Entity entity, float limbSwingTicks, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (!(entity instanceof AbstractClientPlayer)) return;

        RenderHandler.onModelRender((AbstractClientPlayer) entity, limbSwingTicks, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Inject(method = "setRotationAngles", at = @At("RETURN"))
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof AbstractClientPlayer)) return;

        final ModelBiped model = (ModelBiped) (Object) this;
        RenderUtils.setPlayerSkeletonOffsets(model, (AbstractClientPlayer) entity, scale);
    }
}
