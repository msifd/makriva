package msifeed.makriva.ui;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiListExtended;

public class ShapeListEntry implements GuiListExtended.IGuiListEntry {
    public final Shape shape;
    private final ScreenShapeList screen;

    public ShapeListEntry(ScreenShapeList screen, Shape shape) {
        this.screen = screen;
        this.shape = shape;
    }

    @Override
    public void updatePosition(int slotIndex, int x, int y, float partialTicks) {

    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        final ModelShape model = Makriva.MODELS.getPreviewModel();
        final int color = model != null && model.shape == shape
                ? 0xffaaaaaa
                : 0xffffffff;
        fr.drawString(shape.name, x, y, color);
    }

    @Override
    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        screen.selectShape(shape);
        return false;
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
    }
}
