package msifeed.makriva.mixins.skin;

import com.mojang.authlib.GameProfile;
import msifeed.makriva.Makriva;
import msifeed.makriva.model.Shape;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetworkPlayerInfo.class)
public class NetworkPlayerInfoMixin {
    @Final
    @Shadow
    private GameProfile gameProfile;

    @Inject(method = "getSkinType", at = @At("HEAD"), cancellable = true)
    public void getSkinType(CallbackInfoReturnable<String> cir) {
        final Shape shape = Makriva.MODELS.getShape(gameProfile.getId());
        final String modelType = shape.metadata.get("model");
        if (modelType != null) cir.setReturnValue(modelType);
    }
}
