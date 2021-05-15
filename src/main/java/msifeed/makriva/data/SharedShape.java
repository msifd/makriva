package msifeed.makriva.data;

import java.util.HashMap;
import java.util.Map;

public class SharedShape {
    public static SharedShape DEFAULT_SHARED = new SharedShape();

    static {
        DEFAULT_SHARED.eyeHeight.put(PlayerPose.stand, 1.62f);
        DEFAULT_SHARED.eyeHeight.put(PlayerPose.sneak, 1.62f - 0.08f);
        DEFAULT_SHARED.eyeHeight.put(PlayerPose.sleep, 0.2f);
        DEFAULT_SHARED.eyeHeight.put(PlayerPose.elytraFly, 0.4f);

        DEFAULT_SHARED.boundingBox.put(PlayerPose.stand, new Float[]{0.6f, 1.8f});
        DEFAULT_SHARED.boundingBox.put(PlayerPose.sneak, new Float[]{0.6f, 1.65f});
        DEFAULT_SHARED.boundingBox.put(PlayerPose.sleep, new Float[]{0.2f, 0.2f});
        DEFAULT_SHARED.boundingBox.put(PlayerPose.elytraFly, new Float[]{0.6f, 0.6f});
    }

    public final Map<PlayerPose, Float> eyeHeight = new HashMap<>();
    public final Map<PlayerPose, Float[]> boundingBox = new HashMap<>();

    public float getEyeHeight(PlayerPose pose) {
        return eyeHeight.computeIfAbsent(pose, (p) -> DEFAULT_SHARED.getEyeHeight(p));
    }

    public Float[] getBox(PlayerPose pose) {
        return boundingBox.computeIfAbsent(pose, (p) -> DEFAULT_SHARED.getBox(p));
    }
}
