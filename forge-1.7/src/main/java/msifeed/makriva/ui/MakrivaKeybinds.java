package msifeed.makriva.ui;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.MakrivaShared;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class MakrivaKeybinds {
    private final KeyBinding SHAPES_MENU = new KeyBinding("key.makriva.shapes_menu", Keyboard.KEY_HOME, "key.categories.makriva");
    private final KeyBinding RELOAD_SKINS = new KeyBinding("key.makriva.reload_skins", Keyboard.KEY_END, "key.categories.makriva");

    public MakrivaKeybinds() {
        ClientRegistry.registerKeyBinding(SHAPES_MENU);
        ClientRegistry.registerKeyBinding(RELOAD_SKINS);
    }

    @SubscribeEvent
    public void onKeyTyped(InputEvent.KeyInputEvent event) {
        if (RELOAD_SKINS.isPressed()) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Reload skins..."));
            MakrivaShared.MODELS.invalidateAllSkins();
        }
        if (SHAPES_MENU.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new ScreenShapeList());
        }
    }
}
