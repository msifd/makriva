package msifeed.makriva.mixins.render;

import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.BipedPart;
import msifeed.makriva.model.Shape;
import msifeed.makriva.render.LayerMakrivaBones;
import msifeed.makriva.render.PartSelector;
import msifeed.makriva.render.RenderBridge;
import msifeed.makriva.render.RenderUtils;
import msifeed.makriva.render.model.ModelBone;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(RenderPlayer.class)
public class RenderPlayerMixin {
    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/RenderManager;Z)V", at = @At("RETURN"))
    private void init(RenderManager renderManager, boolean useSmallArms, CallbackInfo ci) {
        final RenderPlayer self = (RenderPlayer) (Object) this;
        self.addLayer(new LayerMakrivaBones(self));
    }

    @ModifyArg(
            method = "doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V",
            at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/entity/RenderLivingBase.doRender (Lnet/minecraft/entity/EntityLivingBase;DDDFF)V"),
            index = 2
    )
    private double adjustYPosOfScaledModel(EntityLivingBase player, double x, double y, double z, float yaw, float ticks) {
        return RenderUtils.adjustYPosOfScaledModel((AbstractClientPlayer) player, y);
    }

    @Inject(method = "setModelVisibilities", at = @At("RETURN"))
    private void setModelVisibilities(AbstractClientPlayer clientPlayer, CallbackInfo ci) {
        final RenderPlayer self = (RenderPlayer) (Object) this;
        final ModelPlayer model = self.getMainModel();
        final Shape shape = MakrivaShared.MODELS.getShape(clientPlayer.getGameProfile().getId());
        for (BipedPart bp : shape.hide) {
            for (ModelRenderer part : PartSelector.findParts(model, bp)) {
                part.showModel = false;
            }
        }
    }

    @Inject(method = "preRenderCallback*", at = @At("RETURN"))
    private void preRenderCallback(AbstractClientPlayer clientPlayer, float partialTickTime, CallbackInfo ci) {
        final Shape shape = MakrivaShared.MODELS.getShape(clientPlayer.getGameProfile().getId());

        ((RenderMixin) this).setShadowSize(0.5f * shape.modelScale);

        if (shape.modelScale != 1) {
            GlStateManager.scale(shape.modelScale, shape.modelScale, shape.modelScale);
        }
    }

    @Inject(method = "renderRightArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableBlend()V"))
    private void renderRightArm(AbstractClientPlayer player, CallbackInfo ci) {
        final UUID uuid = player.getGameProfile().getId();
        final ModelShape model = RenderBridge.getModel(uuid);

        final List<ModelBone> bones = model.handBones.get(EnumHandSide.RIGHT);
        if (bones == null) return;

        for (ModelBone bone : bones) {
            bone.render(0.0625f);
        }
    }

    @Inject(method = "renderLeftArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableBlend()V"))
    private void renderLeftArm(AbstractClientPlayer player, CallbackInfo ci) {
        final UUID uuid = player.getGameProfile().getId();
        final ModelShape model = RenderBridge.getModel(uuid);

        final List<ModelBone> bones = model.handBones.get(EnumHandSide.LEFT);
        if (bones == null) return;

        for (ModelBone bone : bones) {
            bone.render(0.0625f);
        }
    }
}
