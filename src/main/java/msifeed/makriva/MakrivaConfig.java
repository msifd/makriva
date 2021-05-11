package msifeed.makriva;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

@Config(modid = Makriva.MOD_ID)
public class MakrivaConfig {
    public static String shape = "";

    public static void sync() {
        ConfigManager.sync(Makriva.MOD_ID, Config.Type.INSTANCE);
    }
}
