package msifeed.makriva.mixins.skin;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.utils.MinecraftSessionServiceWrap;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@SideOnly(Side.CLIENT)
@Mixin(SkinManager.class)
public abstract class SkinManagerMixin {
    @Accessor("field_152797_e")
    public abstract void setSessionService(MinecraftSessionService service);

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService, CallbackInfo ci) {
        setSessionService(new MinecraftSessionServiceWrap(sessionService));
    }
}
