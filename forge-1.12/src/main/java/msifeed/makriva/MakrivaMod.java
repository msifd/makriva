package msifeed.makriva;

import msifeed.makriva.compat.MakrivaCompat;
import msifeed.makriva.compat.MpmCompat;
import msifeed.makriva.render.ModelManager;
import msifeed.makriva.render.RenderBridge;
import msifeed.makriva.render.RenderContext;
import msifeed.makriva.render.SharedRenderState;
import msifeed.makriva.storage.ShapeStorage;
import msifeed.makriva.sync.NetworkBridge;
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
public class MakrivaMod {
    @EventHandler
    public void init(FMLPreInitializationEvent event) {
        MakrivaShared.CFG = new ConfigWrapper(event.getSuggestedConfigurationFile());
        MakrivaShared.RELAY = new SyncRelay(new NetworkBridge());

        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(DebugOverlay.class);
            MakrivaKeybinds.init();

            MakrivaShared.STORAGE = new ShapeStorage(new StorageBridge());
            MakrivaShared.MODELS = new ModelManager<>(new RenderBridge());
            SharedRenderState.EVAL_CTX = new RenderContext();

            MinecraftForge.EVENT_BUS.register(new ClientEventsHandler());
            MinecraftForge.EVENT_BUS.register(MakrivaShared.MODELS);
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
