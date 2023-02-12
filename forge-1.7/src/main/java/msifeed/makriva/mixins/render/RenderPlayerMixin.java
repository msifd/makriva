package msifeed.makriva.mixins.render;

import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.BipedPart;
import msifeed.makriva.model.Shape;
import msifeed.makriva.render.PartSelector;
import msifeed.makriva.render.RenderBridge;
import msifeed.makriva.render.RenderUtils;
import msifeed.makriva.render.model.ModelBone;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
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

import java.util.UUID;

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
     * Hide hidden body parts
     */
    @Inject(method = "shouldRenderPass(Lnet/minecraft/client/entity/AbstractClientPlayer;IF)I", at = @At("RETURN"))
    private void setModelVisibilities(AbstractClientPlayer clientPlayer, int modelType, float scale, CallbackInfoReturnable<Integer> ci) {
        final RenderPlayer self = (RenderPlayer) (Object) this;
        final ModelBiped model = self.modelBipedMain;
        final Shape shape = MakrivaShared.MODELS.getShape(clientPlayer.getGameProfile().getId());
        for (BipedPart bp : shape.hide) {
            for (ModelRenderer part : PartSelector.findParts(model, bp)) {
                part.showModel = false;
            }
        }
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
     * Render shape model hand in the first person
     */
    @Inject(method = "renderFirstPersonArm", at = @At("RETURN"))
    private void renderFirstPersonArm(EntityPlayer player, CallbackInfo ci) {
        final UUID uuid = player.getGameProfile().getId();
        final ModelShape model = RenderBridge.getModel(uuid);

        for (ModelBone bone : model.handBones) {
            bone.render(0.0625f);
        }
    }
}
