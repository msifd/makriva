package msifeed.makriva.mixins.skin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(Minecraft.class)
public interface MinecraftAssetsMixin {
    @Accessor
    File getFileAssets();
}
