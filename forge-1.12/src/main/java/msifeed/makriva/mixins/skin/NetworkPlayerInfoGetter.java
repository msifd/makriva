package msifeed.makriva.mixins.skin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(NetworkPlayerInfo.class)
public interface NetworkPlayerInfoGetter {
    @Accessor
    Map<MinecraftProfileTexture.Type, ResourceLocation> getPlayerTextures();

    @Accessor
    void setPlayerTexturesLoaded(boolean value);
}
