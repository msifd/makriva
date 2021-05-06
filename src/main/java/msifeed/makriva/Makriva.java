package msifeed.makriva;

import msifeed.makriva.client.DebugOverlay;
import msifeed.makriva.client.MakrivaKeybinds;
import msifeed.makriva.render.ShapeModels;
import msifeed.makriva.storage.ShapeStorage;
import msifeed.makriva.sync.SyncClient;
import msifeed.makriva.sync.SyncRelay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Makriva.MOD_ID)
public class Makriva {
    public static final String MOD_ID = "makriva";

    public static final Logger LOG = LogManager.getLogger(MOD_ID);
    public static final SyncRelay RELAY = new SyncRelay();

    public static ShapeStorage STORAGE;
    public static SyncClient SYNC;
    public static ShapeModels MODELS;

    public Makriva() {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            STORAGE = new ShapeStorage();
            SYNC = new SyncClient();
            MODELS = new ShapeModels();
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        RELAY.init();

        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new DebugOverlay());
            MakrivaKeybinds.init();
            STORAGE.init();
        }
    }
}
