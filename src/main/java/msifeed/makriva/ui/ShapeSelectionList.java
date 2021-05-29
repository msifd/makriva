package msifeed.makriva.ui;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShapeSelectionList extends GuiListExtended {
    private final List<ShapeListEntry> entries = new ArrayList<>();

    public ShapeSelectionList(ScreenShapeList screen, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);

        for (Shape s : Makriva.STORAGE.getShapes().values()) {
            entries.add(new ShapeListEntry(screen, s));
        }

        entries.sort(Comparator.comparing(o -> o.shape.name));
    }

    @Override
    public IGuiListEntry getListEntry(int index) {
        return entries.get(index);
    }

    @Override
    protected int getSize() {
        return entries.size();
    }

    @Override
    public int getListWidth() {
        return 90;
    }

    @Override
    protected int getScrollBarX() {
        return width + 87;
    }

    @Override
    protected void drawSlot(int slotIndex, int xPos, int yPos, int slotHeight, int mouseXIn, int mouseYIn, float partialTicks) {
        if (yPos + slotHeight / 2 < top || yPos > top + height) return;

        super.drawSlot(slotIndex, xPos, yPos, slotHeight, mouseXIn, mouseYIn, partialTicks);
    }

    @Override
    protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) {
    }

    @Override
    protected void drawContainerBackground(Tessellator tessellator) {
        super.drawContainerBackground(tessellator);
    }
}
