package msifeed.makriva;

import msifeed.makriva.compat.MakrivaCompat;
import msifeed.makriva.compat.MpmCompat;
import msifeed.makriva.render.ModelManager;
import msifeed.makriva.storage.ShapeStorage;
import msifeed.makriva.sync.SharedShapes;
import msifeed.makriva.sync.StorageBridge;
import msifeed.makriva.sync.SyncRelay;
import msifeed.makriva.ui.DebugOverlay;
import msifeed.makriva.ui.MakrivaKeybinds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod.EventBusSubscriber
@Mod(modid = MakrivaShared.MOD_ID)
public class Makriva {
    public static final SyncRelay RELAY = new SyncRelay();
    public static final SharedShapes SHARED = new SharedShapes();

    public static MakrivaConfig CFG;
    public static ShapeStorage STORAGE;
    public static ModelManager MODELS;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CFG = new MakrivaConfig(event.getSuggestedConfigurationFile());
        RELAY.init();

        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(DebugOverlay.class);
            MakrivaKeybinds.init();

            STORAGE = new ShapeStorage(new StorageBridge(), CFG.shape);
            MODELS = new ModelManager();
            MinecraftForge.EVENT_BUS.register(MODELS);
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded("moreplayermodels")) {
            MakrivaShared.LOG.info(MakrivaCompat.COMPAT, "MPM found");
            MakrivaCompat.mpm = Loader.isModLoaded("moreplayermodels");
            MinecraftForge.EVENT_BUS.register(MpmCompat.class);
        }
    }
}
