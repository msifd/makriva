package msifeed.makriva.render.model;

import msifeed.makriva.data.Cube;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCube {
    private final ModelQuad[] quads;

    public ModelCube(ModelRenderer renderer, Cube cube) {
        this(renderer,
                cube.uv[0], cube.uv[1],
                cube.pos[0], cube.pos[1], cube.pos[2],
                cube.size[0], cube.size[1], cube.size[2],
                cube.delta, cube.mirror
        );
    }

    public ModelCube(ModelRenderer renderer, float u, float v, float x, float y, float z, float dx, float dy, float dz, float delta, boolean mirror) {
        float x1 = x - delta;
        float y1 = y - delta;
        float z1 = z - delta;
        float x2 = x1 + dx + delta;
        float y2 = y1 + dy + delta;
        float z2 = z1 + dz + delta;

        if (mirror) {
            final float tmp = x2;
            x2 = x1;
            x1 = tmp;
        }

        final float tw = renderer.textureWidth;
        final float th = renderer.textureHeight;

        final PositionTextureVertex vert1 = new PositionTextureVertex(x1, y1, z1, 0, 0);
        final PositionTextureVertex vert2 = new PositionTextureVertex(x2, y1, z1, 0, 8);
        final PositionTextureVertex vert3 = new PositionTextureVertex(x2, y2, z1, 8, 8);
        final PositionTextureVertex vert4 = new PositionTextureVertex(x1, y2, z1, 8, 0);
        final PositionTextureVertex vert5 = new PositionTextureVertex(x1, y1, z2, 0, 0);
        final PositionTextureVertex vert6 = new PositionTextureVertex(x2, y1, z2, 0, 8);
        final PositionTextureVertex vert7 = new PositionTextureVertex(x2, y2, z2, 8, 8);
        final PositionTextureVertex vert8 = new PositionTextureVertex(x1, y2, z2, 8, 0);

        this.quads = new ModelQuad[]{
            new ModelQuad(new PositionTextureVertex[]{vert6, vert2, vert3, vert7}, u + dz + dx, v + dz, u + dz + dx + dz, v + dz + dy, tw, th),
            new ModelQuad(new PositionTextureVertex[]{vert1, vert5, vert8, vert4}, u, v + dz, u + dz, v + dz + dy, tw, th),
            new ModelQuad(new PositionTextureVertex[]{vert6, vert5, vert1, vert2}, u + dz, v, u + dz + dx, v + dz, tw, th),
            new ModelQuad(new PositionTextureVertex[]{vert3, vert4, vert8, vert7}, u + dz + dx, v + dz, u + dz + dx + dx, v, tw, th),
            new ModelQuad(new PositionTextureVertex[]{vert2, vert1, vert4, vert3}, u + dz, v + dz, u + dz + dx, v + dz + dy, tw, th),
            new ModelQuad(new PositionTextureVertex[]{vert5, vert6, vert7, vert8}, u + dz + dx + dz, v + dz, u + dz + dx + dz + dx, v + dz + dy, tw, th),
        };

        if (mirror) {
            for (ModelQuad quad : quads)
                quad.flipFace();
        }
    }

    public void render(BufferBuilder buf, float scale) {
        for (ModelQuad quad : quads) {
            quad.render(buf, scale);
        }
    }
}
