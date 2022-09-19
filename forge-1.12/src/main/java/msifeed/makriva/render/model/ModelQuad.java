package msifeed.makriva.render.model;

import msifeed.makriva.model.Quad;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelQuad {
    private PositionTextureVertex[] vertices = new PositionTextureVertex[4];

    public ModelQuad(ModelRenderer renderer, Quad spec) {
        final float v1 = spec.uv[1];
        final float tw = renderer.textureWidth;
        final float th = renderer.textureHeight;

        if (spec.size[0] == 0) {
            final float u = spec.uv[0];
            final float u1 = spec.mirror ? (float) Math.floor(u + spec.size[2]) : u;
            final float u2 = spec.mirror ? u : (float) Math.floor(u + spec.size[2]);
            final float v2 = (float) Math.floor(v1 + spec.size[1]);

            final float y1 = spec.pos[1] - spec.delta;
            final float z1 = spec.pos[2] - spec.delta;
            final float y2 = spec.pos[1] + spec.size[1] + spec.delta;
            final float z2 = spec.pos[2] + spec.size[2] + spec.delta;
            final float x = spec.pos[0];

            this.vertices[0] = new PositionTextureVertex(x, y1, z1, u2 / tw, v1 / th);
            this.vertices[1] = new PositionTextureVertex(x, y1, z2, u1 / tw, v1 / th);
            this.vertices[2] = new PositionTextureVertex(x, y2, z2, u1 / tw, v2 / th);
            this.vertices[3] = new PositionTextureVertex(x, y2, z1, u2 / tw, v2 / th);
        } else if (spec.size[1] == 0) {
            final float u = spec.uv[0];
            final float u1 = spec.mirror ? (float) Math.floor(u + spec.size[0]) : u;
            final float u2 = spec.mirror ? u : (float) Math.floor(u + spec.size[0]);
            final float v2 = (float) Math.floor(v1 + spec.size[2]);

            final float x1 = spec.pos[0] - spec.delta;
            final float z1 = spec.pos[2] - spec.delta;
            final float x2 = spec.pos[0] + spec.size[0] + spec.delta;
            final float z2 = spec.pos[2] + spec.size[2] + spec.delta;
            final float y = spec.pos[1];

            this.vertices[0] = new PositionTextureVertex(x2, y, z2, u2 / tw, v1 / th);
            this.vertices[1] = new PositionTextureVertex(x1, y, z2, u1 / tw, v1 / th);
            this.vertices[2] = new PositionTextureVertex(x1, y, z1, u1 / tw, v2 / th);
            this.vertices[3] = new PositionTextureVertex(x2, y, z1, u2 / tw, v2 / th);
        } else {
            final float u = spec.uv[0];
            final float u1 = spec.mirror ? (float) Math.floor(u + spec.size[0]) : u;
            final float u2 = spec.mirror ? u : (float) Math.floor(u + spec.size[0]);
            final float v2 = (float) Math.floor(v1 + spec.size[1]);

            final float x1 = spec.pos[0] - spec.delta;
            final float y1 = spec.pos[1] - spec.delta;
            final float x2 = spec.pos[0] + spec.size[0] + spec.delta;
            final float y2 = spec.pos[1] + spec.size[1] + spec.delta;
            final float z = spec.pos[2];

            this.vertices[0] = new PositionTextureVertex(x2, y1, z, u2 / tw, v1 / th);
            this.vertices[1] = new PositionTextureVertex(x1, y1, z, u1 / tw, v1 / th);
            this.vertices[2] = new PositionTextureVertex(x1, y2, z, u1 / tw, v2 / th);
            this.vertices[3] = new PositionTextureVertex(x2, y2, z, u2 / tw, v2 / th);
        }
    }

    public ModelQuad(PositionTextureVertex[] vertices, float u1, float v1, float u2, float v2, float tw, float th) {
        this.vertices[0] = vertices[0].setTexturePosition(u2 / tw, v1 / th);
        this.vertices[1] = vertices[1].setTexturePosition(u1 / tw, v1 / th);
        this.vertices[2] = vertices[2].setTexturePosition(u1 / tw, v2 / th);
        this.vertices[3] = vertices[3].setTexturePosition(u2 / tw, v2 / th);
    }

    public void flipFace() {
        final PositionTextureVertex[] p = new PositionTextureVertex[vertices.length];
        for (int i = 0; i < vertices.length; ++i) {
            p[i] = vertices[vertices.length - i - 1];
        }
        vertices = p;
    }

    public void render(BufferBuilder buf, float scale) {
        final Vec3d vec1 = vertices[1].vector3D.subtractReverse(vertices[0].vector3D);
        final Vec3d vec2 = vertices[1].vector3D.subtractReverse(vertices[2].vector3D);
        final Vec3d normal = vec2.crossProduct(vec1).normalize();

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

        for (PositionTextureVertex p : vertices) {
            buf.pos(p.vector3D.x * scale, p.vector3D.y * scale, p.vector3D.z * scale)
                    .tex(p.texturePositionX, p.texturePositionY)
                    .normal((float) normal.x, (float) normal.y, (float) normal.z)
                    .endVertex();
        }

        Tessellator.getInstance().draw();
    }
}
