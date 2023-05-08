package msifeed.makriva.render;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.BipedPart;
import msifeed.makriva.model.Shape;
import msifeed.makriva.render.model.ModelBone;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

@SideOnly(Side.CLIENT)
public class RenderHandler {
    @SubscribeEvent
    public void onPreRender(RenderPlayerEvent.Pre event) {
        final AbstractClientPlayer player = (AbstractClientPlayer) event.entityPlayer;
        if (player.isInvisible()) return;

        final UUID uuid = player.getGameProfile().getId();
        final ModelShape model = RenderBridge.getModel(uuid);
        if (model.boxList.isEmpty()) return;

        RenderUtils.updateEvalPlayer(player);
        RenderUtils.updateEvalPartialTicks(event.partialRenderTick);

        model.render = event.renderer;
    }

    public static void preModelRender(EntityPlayer player, ModelBiped model) {
        GL11.glPushMatrix();

        // Same sneaking and blending logic as in 1.12 and others
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        if (player.isSneaking()) GL11.glTranslatef(0, 0.2f, 0);

        final UUID uuid = player.getGameProfile().getId();
        final Shape shape = MakrivaShared.MODELS.getShape(uuid);
        RenderUtils.setModelBipedVisibilities(model, shape);
    }

    public static void postModelRender(EntityPlayer player, ModelBiped model) {
        RenderUtils.resetModelBipedVisibilities(model);
        GL11.glPopMatrix();
    }

    public static void onModelRender(EntityPlayer player, float limbSwingTicks, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (player.isInvisible()) return;

        final UUID uuid = player.getGameProfile().getId();
        final ModelShape model = RenderBridge.getModel(uuid);
        if (model.boxList.isEmpty()) return;

        RenderUtils.setModelBipedVisibilities(model.render.modelBipedMain, model.shape);
        RenderUtils.updateEvalPlayerValues(limbSwingTicks, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        RenderUtils.updateEvalScale(scale);
        model.animationState.update();

        model.render(player, limbSwingTicks, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    /**
     * @return true if original hand should be hidden
     */
    public static boolean renderFirstPersonArm(AbstractClientPlayer player) {
        final UUID uuid = player.getGameProfile().getId();
        final ModelShape model = RenderBridge.getModel(uuid);

        if (!model.handBones.isEmpty()) {
            RenderUtils.updateEvalPlayer(player);
            RenderUtils.updateEvalScale(0.0625f);
            model.animationState.update();

            for (ModelBone bone : model.handBones) {
                bone.render(0.0625f);
            }
        }

        return model.shape.hide.contains(BipedPart.right_arm);
    }
}
