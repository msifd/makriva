package msifeed.makriva.render.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.MakrivaCommons;
import msifeed.makriva.model.BipedPart;
import msifeed.makriva.model.Bone;
import msifeed.makriva.model.PlayerPose;
import msifeed.makriva.model.Shape;
import msifeed.makriva.render.AnimationState;
import msifeed.makriva.render.IShapeModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ModelShape extends ModelBase implements IShapeModel {
    public final Shape shape;
    public final Map<String, ResourceLocation> textures = new HashMap<>();
    public final List<ModelBone> handBones = new ArrayList<>();

    public final AnimationState animationState;
    public RenderPlayer render;

    public ModelShape(RenderPlayer render, Shape shape) {
        this.render = render;
        this.shape = shape;
        this.animationState = new AnimationState(shape.animation);

        final List<ModelRenderer> firstLevelBones = new ArrayList<>();
        for (Bone spec : shape.bones) {
            final ModelBone bone = new ModelBone(this, spec);
            firstLevelBones.add(bone);

            // Hand bones to render in first-person
            if (spec.parent == BipedPart.right_arm)
                handBones.add(bone);
//            else if (spec.parent == BipedPart.left_arm)
//                handBones.computeIfAbsent(EnumHandSide.LEFT, (h) -> new ArrayList<>()).add(bone);
        }

        // Removes sub-child bones
        boxList.clear();
        boxList.addAll(firstLevelBones);
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    public float[] getSkeletonOffset(BipedPart part) {
        final float[] specOffset = shape.skeleton.get(part);
        final float[] ani = animationState.getSkeletonOffset(part);
        if (specOffset == null)
            return ani;

        final float[] offset = specOffset.clone();
        offset[0] += ani[0];
        offset[1] += ani[1];
        offset[2] += ani[2];
        return offset;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GL11.glPushMatrix();

        final PlayerPose pose = MakrivaCommons.findPose((EntityPlayer) entity);
        if (entity.isSneaking())
            GL11.glTranslated(0, 0.2, 0);

        // Fixes "levitation" when sitting
        if (pose == PlayerPose.sit) {
            final float[] legOffset = getSkeletonOffset(BipedPart.left_leg);
            if (legOffset[1] != 0)
                GL11.glTranslated(0, -legOffset[1] * scale, 0);
        }

        for (ModelRenderer box : (List<ModelRenderer>) boxList) {
            if (box instanceof ModelBone)
                bindTexture((AbstractClientPlayer) entity, (ModelBone) box);
            box.render(scale);
        }

        GL11.glPopMatrix();
    }

    private void bindTexture(AbstractClientPlayer player, ModelBone bone) {
        final TextureManager tex = Minecraft.getMinecraft().getTextureManager();

        if (bone.spec.texture == null) {
            tex.bindTexture(player.getLocationSkin());
            return;
        }

        final ResourceLocation res = textures.get(bone.spec.texture);
        if (res == null) {
            tex.bindTexture(player.getLocationSkin());
            return;
        }

        tex.bindTexture(res);
    }
}
