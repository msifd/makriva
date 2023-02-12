package msifeed.makriva.mixins.render;

import msifeed.makriva.render.RenderBridge;
import msifeed.makriva.render.model.ModelBone;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.entity.player.EntityPlayer;
import noppes.mpm.client.RenderMPM;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(RenderMPM.class)
public class RenderMpmMixin {
    /**
     * Render shape model hand in the first person
     */
    @Inject(method = "renderFirstPersonArm", at = @At("RETURN"))
    private void renderFirstPersonArm(EntityPlayer player, CallbackInfo ci) {
        final UUID uuid = player.getGameProfile().getId();
        final ModelShape model = RenderBridge.getModel(uuid);

        for (ModelBone bone : model.handBones) {
            bone.render(0.0625f);
        }
    }
}
