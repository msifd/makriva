package msifeed.makriva;

import msifeed.makriva.gui.DebugOverlay;
import msifeed.makriva.render.ShapeModels;
import msifeed.makriva.storage.ShapeStorage;
import msifeed.makriva.sync.ShapeSync;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Makriva.MOD_ID)
public class Makriva {
    public static final String MOD_ID = "makriva";

    public static Logger LOG = LogManager.getLogger(MOD_ID);
    public static ShapeSync SYNC = new ShapeSync();

    @SideOnly(Side.CLIENT)
    public static ShapeStorage STORAGE = new ShapeStorage();
    @SideOnly(Side.CLIENT)
    public static ShapeModels MODELS = new ShapeModels();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(SYNC);

        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new DebugOverlay());
            STORAGE.init();
        }
    }
}
