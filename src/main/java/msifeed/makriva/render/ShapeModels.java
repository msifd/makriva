package msifeed.makriva.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import msifeed.makriva.mixins.skin.NetworkPlayerInfoMixin;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ShapeModels {
    protected final Map<UUID, ModelShape> models = new HashMap<>();

    private static ModelShape build(RenderPlayer render, UUID uuid) {
        final Shape shape = Makriva.SYNC.get(uuid);
        Makriva.LOG.info("Build shape model uuid: {}, checksum: {}", uuid, shape.checksum);

        return new ModelShape(render, shape);
    }

    public ModelShape getOrCreate(RenderPlayer render, UUID uuid) {
        return models.computeIfAbsent(uuid, id -> build(render, uuid));
    }

    @Nullable
    public ModelShape getNullable(UUID uuid) {
        return models.get(uuid);
    }

    public void invalidate(UUID uuid) {
        models.remove(uuid);

        final NetHandlerPlayClient conn = Minecraft.getMinecraft().getConnection();
        if (conn == null) return;

        final NetworkPlayerInfo net = conn.getPlayerInfo(uuid);
        if (net != null) {
            final NetworkPlayerInfoMixin mixin = (NetworkPlayerInfoMixin) net;
            mixin.setPlayerTexturesLoaded(false);
        }
    }
}
