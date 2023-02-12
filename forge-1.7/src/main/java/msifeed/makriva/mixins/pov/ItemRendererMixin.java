package msifeed.makriva.mixins.pov;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@SideOnly(Side.CLIENT)
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    // FIXME: this mixin should fix head in block problem

    /**
     * Use eye height to determine if head is inside solid block
     */
    @ModifyArg(
            method = "renderOverlays",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MathHelper;floor_double(D)I", ordinal = 4)
    )
    public double renderOverlays(double value) {
        return value;
//        return value + 1.62f - Minecraft.getMinecraft().thePlayer.getEyeHeight();
    }
}
