package msifeed.makriva.sync;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.makriva.storage.IStorageBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

@SideOnly(Side.CLIENT)
public class StorageBridge implements IStorageBridge {
    @Override
    public void displayError(String message) {
        final EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        final IChatComponent comp = new ChatComponentText(message);
        comp.getChatStyle().setColor(EnumChatFormatting.RED);
        player.addChatMessage(comp);
    }
}
