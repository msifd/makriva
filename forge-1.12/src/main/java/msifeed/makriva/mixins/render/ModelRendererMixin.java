package msifeed.makriva.mixins.render;

import msifeed.makriva.render.RenderUtils;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

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
