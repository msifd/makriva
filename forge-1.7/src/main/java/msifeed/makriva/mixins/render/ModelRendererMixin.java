package msifeed.makriva.mixins.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.render.RenderUtils;
import net.minecraft.client.model.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@SideOnly(Side.CLIENT)
@Mixin(ModelRenderer.class)
public class ModelRendererMixin {
    /**
     * @reason Allows to postRender for hidden parts
     * @author msifeed
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public void postRender(float scale) {
        final ModelRenderer self = (ModelRenderer) (Object) this;
        RenderUtils.externalTransform(self, scale);
    }
}
