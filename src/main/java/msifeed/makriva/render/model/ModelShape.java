package msifeed.makriva.render.model;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.Bone;
import msifeed.makriva.data.Shape;
import msifeed.makriva.expr.context.EvalContext;
import msifeed.makriva.render.ShapeModels;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelShape extends ModelBase {
    public final RenderPlayer render;
    public final Shape shape;
    public final List<ModelBone> bones = new ArrayList<>();
    public final Map<String, ResourceLocation> textures = new HashMap<>();

    public final EvalContext context = new EvalContext();

    public ModelShape(RenderPlayer render, Shape shape) {
        this.render = render;
        this.shape = shape;

        shape.textures.forEach(this::loadTexture);

        final ModelPlayer modelPlayer = render.getMainModel();
        // TODO: two-phase base model init
        for (Bone bone : shape.bones) {
            final ModelRenderer parent = bone.parent != null
                    ? bone.parent.findPart(modelPlayer)
                    : null;
            final ModelBone model = new ModelBone(this, bone, parent);
            bones.add(model);
        }
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entity.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }

        for (ModelBone bone : bones) {
            bindTexture((AbstractClientPlayer) entity, bone);
            bone.render(scale);
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

        ShapeModels.loadTexture(resource, path, url);
    }
}
