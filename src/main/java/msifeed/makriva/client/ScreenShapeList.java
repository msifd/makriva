package msifeed.makriva.client;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Map;

public class ScreenShapeList extends GuiScreen implements GuiPageButtonList.GuiResponder {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Makriva.MOD_ID, "textures/shapes_menu.png");

    private final int menuWidth = 256;
    private final int menuHeight = 166;
    private int menuX = 0;
    private int menuY = 0;

    private float modelRotation = 0;

    private GuiTextField skinField;

    public ScreenShapeList() {
    }

    @Override
    public void initGui() {
        menuX = (width - menuWidth) / 2;
        menuY = (height - menuHeight) / 2;

        skinField = new GuiTextField(0xf02, fontRenderer, menuX + 76, menuY + 8, 96, 14);
        skinField.setGuiResponder(this);

        buttonList.clear();

        final GuiSlider rotation = new GuiSlider(this, 0xf01, menuX + 175, menuY + 137, "", -180, 180, modelRotation, (id, name, value) -> String.format("%.0f", value));
        rotation.setWidth(74);
        buttonList.add(rotation);

        int btnIdx = 0xa02;
        int btnY = menuY + 8;
        for (Map.Entry<String, Shape> e : Makriva.STORAGE.getShapes().entrySet()) {
            buttonList.add(new GuiButton(btnIdx, menuX + 8, btnY, 49, 20, e.getKey()));
            btnY += 21;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground();
        drawModel(mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
        skinField.drawTextBox();
    }

    private void drawBackground() {
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(menuX, menuY, 0, 0, menuWidth, menuHeight);
    }

    private void drawModel(int mouseX, int mouseY) {
        final int viewWidth = 72;
        final int viewHeight = 116;

//        final float modelWidth = (float) (mc.player.getRenderBoundingBox().getAverageEdgeLength());
        final float modelHeight = (float) (mc.player.getRenderBoundingBox().maxY - mc.player.getRenderBoundingBox().minY);
//        final float modelHeight = (float) mc.player.getRenderBoundingBox().getAverageEdgeLength();
//        final int modelWScale = (int) (viewWidth * 0.9 / (Math.max(2, modelHeight)));
        final int modelScale = (int) (viewHeight * 0.8 / modelHeight);

        final int modelX = menuX + 176 + viewWidth / 2;
        final int modelY = menuY + 7 + viewHeight;
        final float modelMouseX = modelX - mouseX;
        final float modelMouseY = modelY - mouseY - mc.player.eyeHeight * modelScale;

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1, 1, 1, 1);

        GlStateManager.pushMatrix();
        GlStateManager.translate(modelX, modelY, 50);
        GlStateManager.scale(-modelScale, modelScale, modelScale);
        GlStateManager.rotate(180, 0, 0, 1);

        final AbstractClientPlayer self = mc.player;
        float f1 = self.prevRenderYawOffset;
        float f2 = self.prevRotationYawHead;
        float f3 = self.prevRotationPitch;
        float f4 = self.rotationPitch;
        float f5 = self.rotationYaw;

        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) -Math.atan(modelMouseY / 40.0F) * 20, 1, 0, 0);
        self.prevRenderYawOffset = -modelRotation;
        self.prevRotationYawHead = -modelRotation + (float) Math.atan(modelMouseX / 40d) * 40;
        self.prevRotationPitch = (float) -Math.atan(modelMouseY / 40d) * 20;
        self.rotationPitch = (float) -Math.atan(modelMouseY / 40d) * 20;
        self.rotationYaw = self.prevRotationYawHead;

        final RenderManager manager = mc.getRenderManager();
        manager.setPlayerViewY(180);
        manager.setRenderShadow(false);
        manager.renderEntity(self, 0, 0, 0, 0, 0, false);
        manager.setRenderShadow(true);

        self.prevRenderYawOffset = f1;
        self.prevRotationYawHead = f2;
        self.prevRotationPitch = f3;
        self.rotationPitch = f4;
        self.rotationYaw = f5;

        GlStateManager.popMatrix();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 0xa00 && button.id < 0xf00) {
            Makriva.STORAGE.setCurrentShape(button.displayString);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        skinField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        skinField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void setEntryValue(int id, boolean value) {
    }

    @Override
    public void setEntryValue(int id, float value) {
        switch (id) {
            case 0xf01:
                this.modelRotation = value;
                break;
        }
    }

    @Override
    public void setEntryValue(int id, String value) {

    }
}
