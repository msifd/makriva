package msifeed.makriva.render.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.model.Cube;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.Tessellator;

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
        float x2 = x1 + dx + delta * 2;
        float y2 = y1 + dy + delta * 2;
        float z2 = z1 + dz + delta * 2;

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

        final float u2 = (float) Math.floor(u + dz);
        final float u3 = (float) Math.floor(u + dz + dx);
        final float uz = (float) Math.floor(u + dz + dz + dx);
        final float ux = (float) Math.floor(u + dz + dx + dx);
        final float u5 = (float) Math.floor(u + dz + dz + dx + dx);
        final float v2 = (float) Math.floor(v + dz);
        final float v3 = (float) Math.floor(v + dz + dy);

        this.quads = new ModelQuad[]{
                new ModelQuad(new PositionTextureVertex[]{vert6, vert2, vert3, vert7}, u3, v2, uz, v3, tw, th),
                new ModelQuad(new PositionTextureVertex[]{vert1, vert5, vert8, vert4}, u, v2, u2, v3, tw, th),
                new ModelQuad(new PositionTextureVertex[]{vert6, vert5, vert1, vert2}, u2, v, u3, v2, tw, th),
                new ModelQuad(new PositionTextureVertex[]{vert3, vert4, vert8, vert7}, u3, v2, ux, v, tw, th),
                new ModelQuad(new PositionTextureVertex[]{vert2, vert1, vert4, vert3}, u2, v2, u3, v3, tw, th),
                new ModelQuad(new PositionTextureVertex[]{vert5, vert6, vert7, vert8}, uz, v2, u5, v3, tw, th),
        };

        if (mirror) {
            for (ModelQuad quad : quads)
                quad.flipFace();
        }
    }

    public void render(Tessellator buf, float scale) {
        for (ModelQuad quad : quads) {
            quad.render(buf, scale);
        }
    }
}
