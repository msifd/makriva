package msifeed.makriva.utils;

import msifeed.makriva.MakrivaShared;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class FilesystemTextureLoader implements Runnable {
    private final ThreadDownloadImageData downloader;
    private final File file;
    private final IImageBuffer imageBuffer;

    public FilesystemTextureLoader(ThreadDownloadImageData downloader, File file, IImageBuffer imageBuffer) {
        this.downloader = downloader;
        this.file = file;
        this.imageBuffer = imageBuffer;
    }

    @Override
    public void run() {
        try {
            BufferedImage bufferedimage = ImageIO.read(file);
            if (imageBuffer != null) {
                bufferedimage = imageBuffer.parseUserSkin(bufferedimage);
            }

            downloader.setBufferedImage(bufferedimage);
        } catch (IOException e) {
            MakrivaShared.LOG.warn("Can't load skin {}. Error: {}", file, e);
        }
    }
}
