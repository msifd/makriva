package msifeed.makriva.render;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.BipedPart;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;

import java.util.UUID;

public class StatureHandler {
    public static void setPlayerSkeletonOffsets(ModelPlayer biped, AbstractClientPlayer entity, float scale) {
        final UUID uuid = entity.getGameProfile().getId();
        final ModelShape model = Makriva.MODELS.getModelWithoutBuild(uuid);
        if (model == null) return;

        for (BipedPart bp : BipedPart.values()) {
            final ModelRenderer[] parts = PartSelector.findParts(biped, bp);
            final float[] offset = model.getSkeletonOffset(bp);
            for (ModelRenderer part : parts) {
                part.offsetX = offset[0] * scale;
                part.offsetY = offset[1] * scale;
                part.offsetZ = offset[2] * scale;
            }
        }
    }

    public static void setBipedSkeletonOffsets(ModelBiped biped, AbstractClientPlayer entity, float scale) {
        final ModelShape model = Makriva.MODELS.getModelWithoutBuild(entity.getUniqueID());
        if (model == null) return;

        for (BipedPart bp : BipedPart.values()) {
            final ModelRenderer part = PartSelector.findPart(biped, bp);
            final float[] offset = model.getSkeletonOffset(bp);
            part.offsetX = offset[0] * scale;
            part.offsetY = offset[1] * scale;
            part.offsetZ = offset[2] * scale;
        }
    }
}
