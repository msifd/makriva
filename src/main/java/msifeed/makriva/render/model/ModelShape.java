package msifeed.makriva.render.model;

import msifeed.makriva.data.Bone;
import msifeed.makriva.data.Shape;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelShape extends ModelBase {
    public final RenderPlayer render;
    public final Shape shape;

    public final List<Map<String, ModelBone>> groups = new ArrayList<>();

    public ModelShape(RenderPlayer render, Shape shape) {
        this.render = render;
        this.shape = shape;

        final ModelPlayer modelPlayer = render.getMainModel();

        // TODO: two-phase base model init
        for (Map<String, Bone> bones : shape.groups) {
            final Map<String, ModelBone> group = new HashMap<>();
            for (Map.Entry<String, Bone> e : bones.entrySet()) {
                final ModelBone model = new ModelBone(this, e.getValue());
                model.setParent(modelPlayer.bipedBody);
                group.put(e.getKey(), model);
            }
            groups.add(group);
        }
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        for (Map<String, ModelBone> bones : groups) {
            for (ModelBone bone : bones.values()) {
                bone.render(scale);
            }
        }
    }
}
