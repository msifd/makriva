package msifeed.makriva.mixins.skin;

import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

@SideOnly(Side.CLIENT)
@Mixin(ImageBufferDownload.class)
public class ImageBufferDownloadMixin {
    @Shadow
    private int[] imageData;
    @Shadow
    private int imageWidth;
    @Shadow
    private int imageHeight;

    /**
     * @author msifeed
     * @reason Removes skin size, format and transparency restrictions
     */
    @Nullable
    @Overwrite
    public BufferedImage parseUserSkin(BufferedImage image) {
        if (image == null) return null;

        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();

        final BufferedImage bufferedimage = new BufferedImage(this.imageWidth, this.imageHeight, BufferedImage.TYPE_INT_ARGB);
        final Graphics graphics = bufferedimage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        this.imageData = ((DataBufferInt) bufferedimage.getRaster().getDataBuffer()).getData();

        return bufferedimage;
    }
}
