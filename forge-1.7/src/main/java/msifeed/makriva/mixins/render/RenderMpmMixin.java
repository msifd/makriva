package msifeed.makriva.mixins.render;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.compat.MpmCompat;
import msifeed.makriva.model.Shape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import noppes.mpm.client.RenderMPM;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.URL;

@SideOnly(Side.CLIENT)
@Pseudo
@Mixin(RenderMPM.class)
public class RenderMpmMixin {
    @Inject(
            method = "loadResource",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;getUrl()Ljava/lang/String;",
                    shift = At.Shift.BEFORE
            ),
            remap = false
    )
    private void rememberSkinUrl(AbstractClientPlayer player, CallbackInfoReturnable<ResourceLocation> cir) {
        final Shape shape = MakrivaShared.MODELS.getShape(player.getGameProfile().getId());
        final URL skin = shape.textures.get("skin");

        MpmCompat.SKIN_URL = skin != null ? skin.toString() : null;
    }

    @Redirect(
            method = "loadResource",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;getUrl()Ljava/lang/String;"
            ),
            remap = false
    )
    private String replaceSkinUrl(MinecraftProfileTexture instance) {
        if (MpmCompat.SKIN_URL != null) return MpmCompat.SKIN_URL;
        return instance.getUrl();
    }
}
