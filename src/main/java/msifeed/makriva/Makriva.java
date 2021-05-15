package msifeed.makriva;

import msifeed.makriva.client.DebugOverlay;
import msifeed.makriva.client.MakrivaKeybinds;
import msifeed.makriva.render.ModelManager;
import msifeed.makriva.storage.ShapeStorage;
import msifeed.makriva.sync.ServerShapes;
import msifeed.makriva.sync.SyncRelay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = Makriva.MOD_ID)
public class Makriva {
    public static final String MOD_ID = "makriva";

    public static final Logger LOG = LogManager.getLogger(MOD_ID);
    public static final SyncRelay RELAY = new SyncRelay();
    public static final ServerShapes SHARED_SHAPES = new ServerShapes();

    public static ShapeStorage STORAGE;
    public static ModelManager MODELS;

    public Makriva() {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            STORAGE = new ShapeStorage();
            MODELS = new ModelManager();
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        RELAY.init();

        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(DebugOverlay.class);
            MinecraftForge.EVENT_BUS.register(MODELS);
            MakrivaKeybinds.init();
            STORAGE.init();
        }
    }

    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.PostConfigChangedEvent event) {
        MakrivaConfig.sync();
    }
}
