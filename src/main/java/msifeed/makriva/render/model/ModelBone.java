package msifeed.makriva.render.model;

import msifeed.makriva.data.Bone;
import msifeed.makriva.data.Box;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelBone extends ModelRenderer {
    public final ModelBase base;
    public ModelRenderer parent;

    public ModelBone(ModelBase base, Bone bone) {
        super(base);
        this.base = base;

        for (Box box : bone.boxes) {
            final ModelRenderer cube = new ModelRenderer(base, box.uv[0], box.uv[1]);

            cube.cubeList.add(new ModelBox(cube,
                    box.uv[0], box.uv[1],
                    box.pos[0], box.pos[1], box.pos[2],
                    box.size[0], box.size[1], box.size[2],
                    box.delta, box.mirrored));
            cube.setRotationPoint(box.rotationPoint[0], box.rotationPoint[1], box.rotationPoint[2]);

            cube.offsetX = box.offset[0];
            cube.offsetY = box.offset[1];
            cube.offsetZ = box.offset[2];

            cube.mirror = box.mirrored;

            addChild(cube);
        }
    }

    public void setParent(ModelRenderer parent) {
        this.parent = parent;
    }

    @Override
    public void render(float scale) {
        cube.rotateAngleX = bone.rotation[0];
        cube.rotateAngleY = bone.rotation[1];
        cube.rotateAngleZ = bone.rotation[2];

        super.render(scale);
    }
}
