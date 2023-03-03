package msifeed.makriva.mixins.skin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.nio.file.Paths;

/**
 * Adds support of local file skins via URL like "file:makriva/skin.png"
 */
@SideOnly(Side.CLIENT)
@Mixin(ThreadDownloadImageData.class)
public class ThreadDownloadImageDataMixin {
    @Mutable
    @Final
    @Shadow
    private File field_152434_e;


    @Inject(method = "<init>", at = @At("RETURN"))
    protected void constructor(File cacheFile, String url, ResourceLocation res, IImageBuffer buffer, CallbackInfo ci) {
        if (!url.startsWith("file:")) return;

        this.field_152434_e = Paths.get(url.replace("file:", "")).toFile();
    }
}
