package msifeed.makriva.mixins.render;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelPlayer.class)
public interface ModelPlayerGetterMixin {
    @Accessor
    ModelRenderer getBipedCape();

    @Accessor
    boolean isSmallArms();
}
