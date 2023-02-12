package msifeed.makriva.mixins.skin;

import msifeed.makriva.utils.FilesystemTextureLoader;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Adds support of local file skins via URL like "file:makriva/skin.png"
 */
@SideOnly(Side.CLIENT)
@Mixin(ThreadDownloadImageData.class)
public class ThreadDownloadImageDataMixin {
    @Final
    @Shadow
    private static AtomicInteger TEXTURE_DOWNLOADER_THREAD_ID;

    @Final
    @Shadow
    private String imageUrl;

    @Final
    @Shadow
    private IImageBuffer imageBuffer;

    @Shadow
    private Thread imageThread;

    @Inject(method = "loadTextureFromServer", at = @At("HEAD"), cancellable = true)
    protected void loadTextureFromServer(CallbackInfo ci) {
        if (!imageUrl.startsWith("file:")) return;

        final ThreadDownloadImageData self = (ThreadDownloadImageData) (Object) this;
        final File file = Paths.get(imageUrl.replace("file:", "")).toFile();

        imageThread = new Thread(
                new FilesystemTextureLoader(self, file, imageBuffer),
                "Local Texture Loader #" + TEXTURE_DOWNLOADER_THREAD_ID.incrementAndGet());
        imageThread.setDaemon(true);
        imageThread.start();

        ci.cancel();
    }
}
