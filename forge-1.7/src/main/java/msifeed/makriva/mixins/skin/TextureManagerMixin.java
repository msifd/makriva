package msifeed.makriva.mixins.skin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@SideOnly(Side.CLIENT)
@Mixin(TextureManager.class)
public interface TextureManagerMixin {
    @Accessor
    Map<ResourceLocation, ITextureObject> getMapTextureObjects();
}
