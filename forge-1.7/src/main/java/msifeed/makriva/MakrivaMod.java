package msifeed.makriva;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import msifeed.makriva.compat.MakrivaCompat;
import msifeed.makriva.compat.MpmCompat;
import msifeed.makriva.render.*;
import msifeed.makriva.storage.ShapeStorage;
import msifeed.makriva.sync.NetworkBridge;
import msifeed.makriva.sync.StorageBridge;
import msifeed.makriva.sync.SyncRelay;
import msifeed.makriva.ui.DebugOverlay;
import msifeed.makriva.ui.MakrivaKeybinds;
import net.minecraftforge.common.MinecraftForge;

/**
 * Known bugs:
 * - [ ] textures don't work properly outside dev env
 * - [x] you bump your head when unsneak in tight spaces
 * - [x] makriva gui is dark at night
 * - [x] mpm can't do sleeping pose
 * - [ ] Support mpm-ari
 */
@Mod(modid = MakrivaShared.MOD_ID)
public class MakrivaMod {
    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        MakrivaShared.CFG = new ConfigWrapper(event.getSuggestedConfigurationFile());
        MakrivaShared.RELAY = new SyncRelay(new NetworkBridge());

        if (FMLCommonHandler.instance().getSide().isClient()) {
            MakrivaShared.STORAGE = new ShapeStorage(new StorageBridge());
            MakrivaShared.MODELS = new ModelManager<>(new RenderBridge());
            SharedRenderState.EVAL_CTX = new RenderContext();

            MinecraftForge.EVENT_BUS.register(new DebugOverlay());
            MinecraftForge.EVENT_BUS.register(new RenderHandler());
            FMLCommonHandler.instance().bus().register(new MakrivaKeybinds());
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded("moreplayermodels")) {
            MakrivaShared.LOG.info(MakrivaCompat.COMPAT, "MPM found");
            MakrivaCompat.mpm = Loader.isModLoaded("moreplayermodels");
            MinecraftForge.EVENT_BUS.register(new MpmCompat());
        }
    }
}