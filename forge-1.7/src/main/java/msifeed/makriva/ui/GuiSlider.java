package msifeed.makriva.ui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import java.util.function.BiConsumer;

@SideOnly(Side.CLIENT)
public class GuiSlider extends GuiButton {
    private float sliderPosition = 1.0F;
    public boolean isMouseDown;
    private final String name;
    private final float min;
    private final float max;
    private final GuiSlider.FormatHelper formatHelper;
    private final BiConsumer<Integer, Float> responder;

    public GuiSlider(BiConsumer<Integer, Float> responder, int idIn, int x, int y, String nameIn, float minIn, float maxIn, float defaultValue, GuiSlider.FormatHelper formatter) {
        super(idIn, x, y, 150, 20, "");
        this.name = nameIn;
        this.min = minIn;
        this.max = maxIn;
        this.sliderPosition = (defaultValue - minIn) / (maxIn - minIn);
        this.formatHelper = formatter;
        this.responder = responder;
        this.displayString = this.getDisplayString();
    }

    public float getSliderValue() {
        return this.min + (this.max - this.min) * this.sliderPosition;
    }

    public void setSliderValue(float value, boolean notifyResponder) {
        this.sliderPosition = (value - this.min) / (this.max - this.min);
        this.displayString = this.getDisplayString();

        if (notifyResponder) {
            this.responder.accept(this.id, this.getSliderValue());
        }
    }

    public float getSliderPosition() {
        return this.sliderPosition;
    }

    private String getDisplayString() {
        return this.formatHelper == null ? I18n.format(this.name) + ": " + this.getSliderValue() : this.formatHelper.getText(this.id, I18n.format(this.name), this.getSliderValue());
    }

    public int getHoverState(boolean mouseOver) {
        return 0;
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.isMouseDown) {
                this.sliderPosition = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);

                if (this.sliderPosition < 0.0F) {
                    this.sliderPosition = 0.0F;
                }

                if (this.sliderPosition > 1.0F) {
                    this.sliderPosition = 1.0F;
                }

                this.displayString = this.getDisplayString();
                this.responder.accept(this.id, this.getSliderValue());
            }

            GL11.glColor4f(1, 1, 1, 1);
            this.drawTexturedModalRect(this.xPosition + (int) (this.sliderPosition * (float) (this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int) (this.sliderPosition * (float) (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    public void setSliderPosition(float position) {
        this.sliderPosition = position;
        this.displayString = this.getDisplayString();
        this.responder.accept(this.id, this.getSliderValue());
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderPosition = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);

            if (this.sliderPosition < 0.0F) {
                this.sliderPosition = 0.0F;
            }

            if (this.sliderPosition > 1.0F) {
                this.sliderPosition = 1.0F;
            }

            this.displayString = this.getDisplayString();
            this.responder.accept(this.id, this.getSliderValue());
            this.isMouseDown = true;
            return true;
        } else {
            return false;
        }
    }

    public void mouseReleased(int mouseX, int mouseY) {
        this.isMouseDown = false;
    }

    @SideOnly(Side.CLIENT)
    public interface FormatHelper {
        String getText(int id, String name, float value);
    }
}
