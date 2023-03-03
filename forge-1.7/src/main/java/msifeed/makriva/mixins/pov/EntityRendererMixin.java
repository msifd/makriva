package msifeed.makriva.mixins.pov;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.utils.PlayerDimensionsMath;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SideOnly(Side.CLIENT)
@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    /**
     * Use dynamic eye height instead of fixed one.
     */
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(
            method = "orientCamera",
            ordinal = 1,
            at = @At(value = "STORE", ordinal = 0),
            require = 1
    )
    private float orientCamera(float eyesOffset) {
        return PlayerDimensionsMath.modifyEyeOffsetToOrientCamera(eyesOffset);
    }
}
