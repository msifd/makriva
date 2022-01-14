package msifeed.makriva.mixins.skin;

import com.mojang.authlib.GameProfile;
import msifeed.makriva.Makriva;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkPlayerInfo.class)
public class NetworkPlayerInfoMixin {
    @Final
    @Shadow
    private GameProfile gameProfile;

    @Inject(method = "loadPlayerTextures", at = @At("HEAD"), cancellable = true)
    protected void loadPlayerTextures(CallbackInfo ci) {
        // Pause skin loading until we get shape
        if (!Makriva.MODELS.hasShape(gameProfile.getId())) {
            ci.cancel();
        }
    }
}
