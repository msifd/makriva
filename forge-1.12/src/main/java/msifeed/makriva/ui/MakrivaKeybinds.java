package msifeed.makriva.ui;

import msifeed.makriva.Makriva;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class MakrivaKeybinds {
    private static final KeyBinding SHAPES_MENU = new KeyBinding("key.makriva.shapes_menu", KeyConflictContext.IN_GAME, Keyboard.KEY_HOME, "key.categories.makriva");
    private static final KeyBinding RELOAD_SKINS = new KeyBinding("key.makriva.reload_skins", KeyConflictContext.IN_GAME, KeyModifier.CONTROL, Keyboard.KEY_HOME, "key.categories.makriva");

    public static void init() {
        MinecraftForge.EVENT_BUS.register(MakrivaKeybinds.class);
        ClientRegistry.registerKeyBinding(SHAPES_MENU);
        ClientRegistry.registerKeyBinding(RELOAD_SKINS);
    }

    @SubscribeEvent
    public static void onKeyTyped(InputEvent.KeyInputEvent event) {
        if (RELOAD_SKINS.isKeyDown()) {
            Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString("Reload skins..."), true);
            Makriva.MODELS.reloadAllSkins();
        } else if (SHAPES_MENU.isKeyDown()) {
            Minecraft.getMinecraft().displayGuiScreen(new ScreenShapeList());
        }
    }
}
