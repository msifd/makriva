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
//@Mixin(targets = "net/minecraft/client/resources/SkinManager$3")
@Mixin(SkinManager.class)
public abstract class SkinManagerMixin {
    @Final
    @Shadow
    private MinecraftSessionService sessionService;

    @Accessor
    public abstract void setSessionService(MinecraftSessionService service);

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService, CallbackInfo ci) {
        setSessionService(new MinecraftSessionServiceWrap(sessionService));
    }

//    @ModifyVariable(
//            method = "run",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;addScheduledTask(Ljava/lang/Runnable;)Lcom/google/common/util/concurrent/ListenableFuture;")
//    )
//    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> addTextures(Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map) {
//        final Map<String, String> meta = new HashMap<>();
//        meta.put("model", "slim");
//        map.put(MinecraftProfileTexture.Type.SKIN, new MinecraftProfileTexture("https://skins.ariadna.su/Korry.png", meta));
//
//        return map;
//    }
}
