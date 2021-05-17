package msifeed.makriva.mixins.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.BipedPart;
import msifeed.makriva.expr.IExpr;
import msifeed.makriva.expr.context.EvalContext;
import msifeed.makriva.render.PartSelector;
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
    @Final
    @Shadow
    protected RenderLivingBase<?> livingEntityRenderer;

    private static final String TRANSLATE = "Lnet/minecraft/client/renderer/entity/layers/LayerHeldItem;translateToHand(Lnet/minecraft/util/EnumHandSide;)V";

    @Inject(method = "renderHeldItem", at = @At(value = "INVOKE", target = TRANSLATE))
    private void renderHeldItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType type, EnumHandSide hand, CallbackInfo ci) {
        if (!(livingEntityRenderer instanceof RenderPlayer)) return;
        if (!(entity instanceof AbstractClientPlayer)) return;

        final RenderPlayer render = (RenderPlayer) livingEntityRenderer;

        final UUID uuid = ((AbstractClientPlayer) entity).getGameProfile().getId();
        final ModelShape model = Makriva.MODELS.getModel(render, uuid);

        final BipedPart handPart = hand == EnumHandSide.RIGHT ? BipedPart.right_arm : BipedPart.left_arm;
        final ModelRenderer part = PartSelector.findPart(render.getMainModel(), handPart);

        final EvalContext ctx = model.context;
        ctx.update((AbstractClientPlayer) entity);

        final float scale = ctx.renderParams.scale;

        final IExpr[] exprs = model.shape.skeleton.get(handPart);
        if (exprs != null) {
            part.offsetX = ctx.num(exprs[0]) * scale;
            part.offsetY = ctx.num(exprs[1]) * scale;
            part.offsetZ = ctx.num(exprs[2]) * scale;
        } else {
            part.offsetX = 0;
            part.offsetY = 0;
            part.offsetZ = 0;
        }

        if (part.isHidden || !part.showModel) {
            RenderUtils.externalTransform(part, scale);
        }
    }
}
