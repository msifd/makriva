package msifeed.makriva.sync;

import msifeed.makriva.Makriva;
import msifeed.makriva.model.Shape;
import msifeed.makriva.storage.IStorageBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class StorageBridge implements IStorageBridge {
    @Override
    public void currentShapeChanged(@Nullable String newShapeName) {
        Makriva.CFG.setShape(newShapeName != null ? newShapeName : Shape.DEFAULT.name);
        Makriva.CFG.save();
    }

    @Override
    public void uploadShape(Shape shape) {
        SyncRelay.upload(shape);
    }

    @Override
    public void displayError(String message) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;

        final ITextComponent comp = new TextComponentString(message);
        comp.getStyle().setColor(TextFormatting.RED);
        player.sendMessage(comp);
    }
}
