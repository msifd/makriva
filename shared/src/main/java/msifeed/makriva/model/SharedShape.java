package msifeed.makriva.model;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

public class SharedShape {
    public static SharedShape DEFAULT_SHARED = new SharedShape();

    static {
        DEFAULT_SHARED.eyeHeight.put(PlayerPose.stand, 1.62f);
        DEFAULT_SHARED.eyeHeight.put(PlayerPose.sneak, 1.62f - 0.08f);
        DEFAULT_SHARED.eyeHeight.put(PlayerPose.sit, 1.62f);
        DEFAULT_SHARED.eyeHeight.put(PlayerPose.sleep, 0.2f);
        DEFAULT_SHARED.eyeHeight.put(PlayerPose.elytraFly, 0.4f);
        DEFAULT_SHARED.eyeHeight.put(PlayerPose.crawl, 0.4f);

        DEFAULT_SHARED.boundingBox.put(PlayerPose.stand, new float[]{0.6f, 1.8f});
        DEFAULT_SHARED.boundingBox.put(PlayerPose.sneak, new float[]{0.6f, 1.65f});
        DEFAULT_SHARED.boundingBox.put(PlayerPose.sit, new float[]{0.6f, 1.8f});
        DEFAULT_SHARED.boundingBox.put(PlayerPose.sleep, new float[]{0.2f, 0.2f});
        DEFAULT_SHARED.boundingBox.put(PlayerPose.elytraFly, new float[]{0.6f, 0.6f});
        DEFAULT_SHARED.boundingBox.put(PlayerPose.crawl, new float[]{0.6f, 0.6f});
    }

    public final Map<PlayerPose, Float> eyeHeight = new EnumMap<>(PlayerPose.class);
    public final Map<PlayerPose, float[]> boundingBox = new EnumMap<>(PlayerPose.class);
    public float modelScale = 1;

    public float getEyeHeight(PlayerPose pose) {
        if (eyeHeight.containsKey(pose))
            return eyeHeight.get(pose);
        else
            return DEFAULT_SHARED.eyeHeight.get(pose);
    }

    @Nonnull
    public float[] getBox(PlayerPose pose) {
        float[] box = getRawBox(pose);
        if (modelScale != 1) {
            box = box.clone();
            box[0] *= modelScale;
            box[1] *= modelScale;
        }
        return box;
    }

    @Nonnull
    private float[] getRawBox(PlayerPose pose) {
        if (boundingBox.containsKey(pose))
            return boundingBox.get(pose);
        else
            return DEFAULT_SHARED.boundingBox.get(pose);
    }
}
