package msifeed.makriva.client;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.Shape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;

public class ScreenShapeList extends GuiScreen implements GuiPageButtonList.GuiResponder {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Makriva.MOD_ID, "textures/shapes_menu.png");

    private final int menuWidth = 256;
    private final int menuHeight = 166;
    private int menuX = 0;
    private int menuY = 0;

    private float modelRotation = 0;
    private String selectedShape = Makriva.STORAGE.getCurrentShape().name;

    private GuiTextField skinField;
    private GuiButton selectBtn;

    @Override
    public void initGui() {
        buttonList.clear();

        menuX = (width - menuWidth) / 2;
        menuY = (height - menuHeight) / 2;

        // Navigation

        int btnY = menuY + 8;
        for (Map.Entry<String, Shape> e : Makriva.STORAGE.getShapes().entrySet()) {
            final GuiButton btn = new GuiButton(0xa01, menuX + 8, btnY, 49, 20, e.getKey());
            btn.enabled = !e.getKey().equals(selectedShape);
            buttonList.add(btn);
            btnY += 21;
        }

        buttonList.add(new GuiButton(0xa02, menuX + 7, menuY + 139, 51, 20, "Open dir"));

        // Model view

        final GuiSlider rotation = new GuiSlider(this, 0xc02, menuX + 175, menuY + 139, "", -180, 180, modelRotation, (id, name, value) -> String.format("%.0f", value));
        rotation.setWidth(74);
        buttonList.add(rotation);

        // Shape controls

        buttonList.add(new GuiButton(0xb01, menuX + 75, menuY + 139, 46, 20, "Edit"));

        selectBtn = new GuiButton(0xb02, menuX + 123, menuY + 139, 50, 20, "Select");
        selectBtn.enabled = false;
        buttonList.add(selectBtn);

        skinField = new GuiTextField(0xb03, fontRenderer, menuX + 76, menuY + 8, 96, 14);
        skinField.setMaxStringLength(256);
        skinField.setText(getSelectedSkinTexture());
        skinField.setGuiResponder(this);
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
    public void onGuiClosed() {
        Makriva.MODELS.clearPreview();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0xa01: {
                for (GuiButton b : buttonList) {
                    if (b.id == 0xa01)
                        b.enabled = true;
                }
                button.enabled = false;

                selectedShape = button.displayString;
                Makriva.MODELS.selectPreview(selectedShape);

                skinField.setText(getSelectedSkinTexture());
                selectBtn.enabled = !Makriva.STORAGE.getCurrentShape().name.equals(selectedShape);
            }
            break;
            case 0xa02:
                Desktop.getDesktop().open(Paths.get(Makriva.MOD_ID).toFile());
                break;
            case 0xb01:
                Desktop.getDesktop().open(Makriva.STORAGE.getShapeFile(selectedShape).toFile());
                break;
            case 0xb02:
                selectBtn.enabled = false;
                Makriva.STORAGE.setCurrentShape(selectedShape);
                break;
        }
    }

    private String getSelectedSkinTexture() {
        final Shape shape = Makriva.STORAGE.getShapes().get(selectedShape);
        if (shape == null) return "";

        final URL url = shape.textures.get("skin");
        return url != null ? url.toString() : "";
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
            case 0xc02:
                modelRotation = value;
                break;
        }
    }

    @Override
    public void setEntryValue(int id, String value) {
        switch (id) {
            case 0xb03:
                // Skin
                break;
        }
    }
}
