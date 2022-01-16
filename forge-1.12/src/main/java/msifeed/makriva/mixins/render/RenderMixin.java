package msifeed.makriva.mixins.render;

import net.minecraft.client.renderer.entity.Render;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Render.class)
public interface RenderMixin {
    @Accessor
    void setShadowSize(float size);
}
