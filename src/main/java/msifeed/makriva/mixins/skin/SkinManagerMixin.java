package msifeed.makriva.mixins.skin;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import msifeed.makriva.utils.MinecraftSessionServiceWrap;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@SideOnly(Side.CLIENT)
@Mixin(SkinManager.class)
public abstract class SkinManagerMixin {
    @Accessor
    public abstract void setSessionService(MinecraftSessionService service);

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService, CallbackInfo ci) {
        setSessionService(new MinecraftSessionServiceWrap(sessionService));
    }
}
