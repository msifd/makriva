package msifeed.makriva.mixins.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.render.RenderHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import noppes.mpm.client.model.ModelMPM;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SideOnly(Side.CLIENT)
@Pseudo
@Mixin(ModelMPM.class)
public class ModelMpmaMixin {
    @Inject(method = "render", at = @At("RETURN"))
    public void render(Entity entity, float limbSwingTicks, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (!(entity instanceof AbstractClientPlayer)) return;

        RenderHandler.onModelRender((AbstractClientPlayer) entity, limbSwingTicks, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    /**
     * Render shape model hand in the first person and hide original arm if requested
     */
    @Inject(method = "renderArms", at = @At("HEAD"), cancellable = true, remap = false)
    public void renderArms(Entity entity, float f, boolean bo, CallbackInfo ci) {
        final boolean armIsHidden = RenderHandler.renderFirstPersonArm((AbstractClientPlayer) entity);
        if (armIsHidden) ci.cancel();
    }
}