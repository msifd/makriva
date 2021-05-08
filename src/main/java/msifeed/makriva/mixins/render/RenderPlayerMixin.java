package msifeed.makriva.mixins.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.BipedPart;
import msifeed.makriva.data.Shape;
import msifeed.makriva.render.LayerMakrivaBones;
import msifeed.makriva.render.StatureHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class RenderPlayerMixin {
    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/RenderManager;Z)V", at = @At("RETURN"))
    private void init(RenderManager renderManager, boolean useSmallArms, CallbackInfo ci) {
        final RenderPlayer self = (RenderPlayer) (Object) this;
        self.addLayer(new LayerMakrivaBones(self));
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    private void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        final RenderPlayer self = (RenderPlayer) (Object) this;
        final ModelPlayer model = self.getMainModel();
        StatureHandler.resetSkeletonOffsets(model);
    }

    @Inject(method = "setModelVisibilities", at = @At("RETURN"))
    private void setModelVisibilities(AbstractClientPlayer clientPlayer, CallbackInfo ci) {
        final RenderPlayer self = (RenderPlayer) (Object) this;
        final ModelPlayer model = self.getMainModel();
        final Shape shape = Makriva.MODELS.getShape(clientPlayer.getGameProfile().getId());
        for (BipedPart bp : shape.hide) {
            final ModelRenderer[] parts = bp.findParts(model);
            if (parts == null) continue;

            for (ModelRenderer part : parts) {
                part.showModel = false;
            }
        }
    }
}
