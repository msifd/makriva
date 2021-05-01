package msifeed.makriva.mixins.skin;

import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NetworkPlayerInfo.class)
public interface NetworkPlayerInfoMixin {
    @Accessor
    void setPlayerTexturesLoaded(boolean value);
}
