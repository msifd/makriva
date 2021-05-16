package msifeed.makriva.render.model;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.Bone;
import msifeed.makriva.data.Shape;
import msifeed.makriva.expr.context.EvalContext;
import msifeed.makriva.render.ModelManager;
import msifeed.makriva.render.PartSelector;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ModelShape extends ModelBase {
    public final RenderPlayer render;
    public final Shape shape;
    public final Map<String, ResourceLocation> textures = new HashMap<>();

    public final EvalContext context = new EvalContext();

    public ModelShape(RenderPlayer render, Shape shape) {
        this.render = render;
        this.shape = shape;

        shape.textures.forEach(this::loadTexture);

        final ModelPlayer modelPlayer = render.getMainModel();
        // TODO: two-phase base model init

        final List<ModelRenderer> firstLevelBones = new ArrayList<>();
        for (Bone spec : shape.bones) {
            final ModelRenderer parent = spec.parent != null
                    ? PartSelector.findPart(modelPlayer, spec.parent)
                    : null;
            firstLevelBones.add(new ModelBone(this, spec, parent));
        }

        // Removes sub-child bones
        boxList.clear();
        boxList.addAll(firstLevelBones);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entity.isSneaking()) {
            GL11.glPushMatrix();
            GlStateManager.translate(0, 0.2, 0);
        }

        for (ModelRenderer box : boxList) {
            if (box instanceof ModelBone)
                bindTexture((AbstractClientPlayer) entity, (ModelBone) box);
            box.render(scale);
        }

        if (entity.isSneaking()) {
            GL11.glPopMatrix();
        }
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
        final ResourceLocation resource = new ResourceLocation(Makriva.MOD_ID, path);
        textures.put(name, resource);

        ModelManager.loadTexture(resource, path, url);
    }
}
