package msifeed.makriva;

import msifeed.makriva.gui.DebugOverlay;
import msifeed.makriva.sync.ShapeSync;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Makriva.MOD_ID, name = Makriva.MOD_NAME)
public class Makriva {
    public static final String MOD_ID = "makriva";
    public static final String MOD_NAME = "Makriva";

    public static Logger LOG = LogManager.getLogger(MOD_NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new DebugOverlay());
        MinecraftForge.EVENT_BUS.register(ShapeSync.INSTANCE);
    }
}
