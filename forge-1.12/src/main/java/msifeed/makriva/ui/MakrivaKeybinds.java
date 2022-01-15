package msifeed.makriva.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
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

    public static void init() {
        MinecraftForge.EVENT_BUS.register(MakrivaKeybinds.class);
        ClientRegistry.registerKeyBinding(SHAPES_MENU);
    }

    @SubscribeEvent
    public static void onKeyTyped(InputEvent.KeyInputEvent event) {
        if (SHAPES_MENU.isKeyDown())
            Minecraft.getMinecraft().displayGuiScreen(new ScreenShapeList());
    }
}