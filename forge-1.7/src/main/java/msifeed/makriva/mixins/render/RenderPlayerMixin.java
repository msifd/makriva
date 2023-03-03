package msifeed.makriva.mixins.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.BipedPart;
import msifeed.makriva.model.Shape;
import msifeed.makriva.render.RenderHandler;
import msifeed.makriva.render.RenderUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SideOnly(Side.CLIENT)
@Mixin(RenderPlayer.class)
public class RenderPlayerMixin {
    /**
     * Adjusts y pos of the model based on model scale and its pose.
     * Works the same way as the sneaking in the injection point.
     */
    @ModifyArg(
            method = "doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RendererLivingEntity;doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V"),
            index = 2
    )
    private double adjustYPosOfScaledModel(EntityLivingBase player, double x, double y, double z, float yaw, float ticks) {
        return RenderUtils.adjustYPosOfScaledModel((AbstractClientPlayer) player, y);
    }

    /**
     * Hides hidden body parts
     */
    @Inject(method = "shouldRenderPass(Lnet/minecraft/client/entity/AbstractClientPlayer;IF)I", at = @At("RETURN"))
    private void setModelVisibilities(AbstractClientPlayer clientPlayer, int modelType, float scale, CallbackInfoReturnable<Integer> ci) {
        final RenderPlayer self = (RenderPlayer) (Object) this;
        final ModelBiped model = self.modelBipedMain;
        final Shape shape = MakrivaShared.MODELS.getShape(clientPlayer.getGameProfile().getId());

        // Check every time to un-hide when shape changes
        model.bipedHead.showModel = !shape.hide.contains(BipedPart.head);
        model.bipedHeadwear.showModel = model.bipedHead.showModel;
        model.bipedBody.showModel = !shape.hide.contains(BipedPart.body);
        model.bipedRightArm.showModel = !shape.hide.contains(BipedPart.right_arm);
        model.bipedLeftArm.showModel = !shape.hide.contains(BipedPart.left_arm);
        model.bipedRightLeg.showModel = !shape.hide.contains(BipedPart.right_leg);
        model.bipedLeftLeg.showModel = !shape.hide.contains(BipedPart.left_leg);
    }

    /**
     * Scale model and its shadow
     */
    @Inject(method = "preRenderCallback*", at = @At("RETURN"))
    private void preRenderCallback(AbstractClientPlayer clientPlayer, float partialTickTime, CallbackInfo ci) {
        final Shape shape = MakrivaShared.MODELS.getShape(clientPlayer.getGameProfile().getId());

        ((RenderMixin) this).setShadowSize(0.5f * shape.modelScale);

        if (shape.modelScale != 1) {
            GL11.glScaled(shape.modelScale, shape.modelScale, shape.modelScale);
        }
    }

    /**
     * Render shape model hand in the first person and hide original arm if requested
     */
    @Inject(
            method = "renderFirstPersonArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V"),
            cancellable = true
    )
    private void renderFirstPersonArm(EntityPlayer player, CallbackInfo ci) {
        final boolean armIsHidden = RenderHandler.renderFirstPersonArm((AbstractClientPlayer) player);
        if (armIsHidden) ci.cancel();
    }
}
