package msifeed.makriva;

import msifeed.makriva.data.Shape;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

@Config(modid = Makriva.MOD_ID, name = "Makriva", category = "")
public class MakrivaConfig {
    @Config.Name("client")
    public static ClientConfig client = new ClientConfig();
    @Config.Name("server")
    public static ServerConfig server = new ServerConfig();

    public static void sync() {
        Makriva.LOG.info("Sync config");
        ConfigManager.sync(Makriva.MOD_ID, Config.Type.INSTANCE);
    }

    public static class ClientConfig {
        @Config.Comment("Current selected shape")
        public String shape = Shape.DEFAULT.name;
    }

    public static class ServerConfig {
        @Config.Comment("Maximal height of player' eye position")
        @Config.RangeDouble(min = 0)
        public float maxEyeHeight = 4.0f;

        @Config.Comment("Minimal height of player' custom bounding box")
        @Config.RangeDouble(min = 0)
        public float minBBHeight = 0.6f;
    }
}
