package msifeed.makriva.mixins.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.BipedPart;
import msifeed.makriva.data.Shape;
import msifeed.makriva.render.LayerMakrivaBones;
import msifeed.makriva.render.PartSelector;
import msifeed.makriva.render.model.ModelBone;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    @Inject(method = "setModelVisibilities", at = @At("RETURN"))
    private void setModelVisibilities(AbstractClientPlayer clientPlayer, CallbackInfo ci) {
        final RenderPlayer self = (RenderPlayer) (Object) this;
        final ModelPlayer model = self.getMainModel();
        final Shape shape = Makriva.MODELS.getShape(clientPlayer.getGameProfile().getId());
        for (BipedPart bp : shape.hide) {
            final ModelRenderer[] parts = PartSelector.findParts(model, bp);
            if (parts == null) continue;

            for (ModelRenderer part : parts) {
                part.showModel = false;
            }
        }
    }

    @Inject(method = "renderRightArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableBlend()V"))
    private void renderRightArm(AbstractClientPlayer player, CallbackInfo ci) {
//        if (player.ticksExisted < 11) return; // Somehow fixes issue with biped parts rotation (e.g. sneak)

        final RenderPlayer self = (RenderPlayer) (Object) this;
        final UUID uuid = player.getGameProfile().getId();
        final ModelShape model = Makriva.MODELS.getModel(self, uuid);

        final List<ModelBone> bones = model.handBones.get(EnumHandSide.RIGHT);
        if (bones == null) return;

        for (ModelBone bone : bones) {
            bone.render(0.0625f);
        }
    }

    @Inject(method = "renderLeftArm", at = @At("HEAD"))
    private void renderLeftArm(AbstractClientPlayer player, CallbackInfo ci) {
        final RenderPlayer self = (RenderPlayer) (Object) this;
        final UUID uuid = player.getGameProfile().getId();
        final ModelShape model = Makriva.MODELS.getModel(self, uuid);

        final List<ModelBone> bones = model.handBones.get(EnumHandSide.LEFT);
        if (bones == null) return;

        for (ModelBone bone : bones) {
            bone.render(0.0625f);
        }
    }
}
