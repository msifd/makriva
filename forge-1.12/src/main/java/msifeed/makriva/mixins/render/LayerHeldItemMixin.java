package msifeed.makriva.mixins.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.model.BipedPart;
import msifeed.makriva.render.PartSelector;
import msifeed.makriva.render.RenderContext;
import msifeed.makriva.render.RenderUtils;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(LayerHeldItem.class)
public class LayerHeldItemMixin {
    private static final String TRANSLATE = "Lnet/minecraft/client/renderer/entity/layers/LayerHeldItem;translateToHand(Lnet/minecraft/util/EnumHandSide;)V";

    @Final
    @Shadow
    protected RenderLivingBase<?> livingEntityRenderer;

    @Inject(method = "doRenderLayer", at = @At("HEAD"))
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (entity instanceof AbstractClientPlayer)
            RenderContext.CTX.update((AbstractClientPlayer) entity);
    }

    @Inject(method = "renderHeldItem", at = @At(value = "INVOKE", target = TRANSLATE))
    private void renderHeldItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType type, EnumHandSide hand, CallbackInfo ci) {
        if (!(livingEntityRenderer instanceof RenderPlayer)) return;
        if (!(entity instanceof AbstractClientPlayer)) return;

        final RenderPlayer render = (RenderPlayer) livingEntityRenderer;

        final UUID uuid = ((AbstractClientPlayer) entity).getGameProfile().getId();
        final ModelShape model = Makriva.MODELS.getModel(render, uuid);

        final BipedPart handPart = hand == EnumHandSide.RIGHT ? BipedPart.right_arm : BipedPart.left_arm;
        final ModelRenderer part = PartSelector.findPart(render.getMainModel(), handPart);

        final float scale = RenderContext.CTX.playerScale();
        final float[] offset = model.getSkeletonOffset(handPart);
        part.offsetX = offset[0] * scale;
        part.offsetY = offset[1] * scale;
        part.offsetZ = offset[2] * scale;

        if (part.isHidden || !part.showModel) {
            RenderUtils.externalTransform(part, scale);
        }
    }
}
