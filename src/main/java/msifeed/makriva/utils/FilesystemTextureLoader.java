package msifeed.makriva.utils;

import msifeed.makriva.Makriva;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
            Makriva.LOG.warn("Can't load skin {}. Error: {}", file, e);
        }
    }
}
