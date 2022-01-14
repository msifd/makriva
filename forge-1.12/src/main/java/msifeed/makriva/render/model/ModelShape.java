package msifeed.makriva.render.model;

import msifeed.makriva.MakrivaCommons;
import msifeed.makriva.render.RenderContext;
import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.BipedPart;
import msifeed.makriva.model.Bone;
import msifeed.makriva.model.PlayerPose;
import msifeed.makriva.model.Shape;
import msifeed.makriva.render.AnimationState;
import msifeed.makriva.render.ModelManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.net.URL;
import java.util.*;

@SideOnly(Side.CLIENT)
public class ModelShape extends ModelBase {
    public final Shape shape;
    public final Map<String, ResourceLocation> textures = new HashMap<>();
    public final Map<EnumHandSide, List<ModelBone>> handBones = new EnumMap<>(EnumHandSide.class);

    public final RenderContext context = new RenderContext();
    public final AnimationState animationState;
    public RenderPlayer render;

    public ModelShape(RenderPlayer render, Shape shape) {
        this.render = render;
        this.shape = shape;
        this.animationState = new AnimationState(shape.animation);

        shape.textures.forEach(this::loadTexture);

        final List<ModelRenderer> firstLevelBones = new ArrayList<>();
        for (Bone spec : shape.bones) {
            final ModelBone bone = new ModelBone(this, spec);
            firstLevelBones.add(bone);

            // Hand bones to render in first-person
            if (spec.parent == BipedPart.right_arm)
                handBones.computeIfAbsent(EnumHandSide.RIGHT, (h) -> new ArrayList<>()).add(bone);
            else if (spec.parent == BipedPart.left_arm)
                handBones.computeIfAbsent(EnumHandSide.LEFT, (h) -> new ArrayList<>()).add(bone);
        }

        // Removes sub-child bones
        boxList.clear();
        boxList.addAll(firstLevelBones);
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
            GlStateManager.translate(0, 0.2, 0);

        // Fixes "levitation" when sitting
        if (pose == PlayerPose.sit) {
            final float[] legOffset = getSkeletonOffset(BipedPart.left_leg);
            if (legOffset[1] != 0)
                GlStateManager.translate(0, -legOffset[1] * scale, 0);
        }

        for (ModelRenderer box : boxList) {
            if (box instanceof ModelBone)
                bindTexture((AbstractClientPlayer) entity, (ModelBone) box);
            box.render(scale);
        }

        GL11.glPopMatrix();
    }

    private void bindTexture(AbstractClientPlayer player, ModelBone bone) {
        if (bone.spec.texture == null) {
            render.bindTexture(player.getLocationSkin());
            return;
        }

        final ResourceLocation res = textures.get(bone.spec.texture);
        if (res == null) {
            render.bindTexture(player.getLocationSkin());
            return;
        }

        render.bindTexture(res);
    }

    private void loadTexture(String name, URL url) {
        if (name.equals("skin") || name.equals("cape") || name.equals("elytra")) return;

        final String path = url.getProtocol() + url.getPath().replace('/', '-');
        final ResourceLocation resource = new ResourceLocation(MakrivaShared.MOD_ID, path);
        textures.put(name, resource);

        ModelManager.loadTexture(resource, path, url);
    }
}
