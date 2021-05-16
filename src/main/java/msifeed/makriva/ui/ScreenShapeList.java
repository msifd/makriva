package msifeed.makriva.ui;

import msifeed.makriva.Makriva;
import msifeed.makriva.data.PlayerPose;
import msifeed.makriva.data.Shape;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;

public class ScreenShapeList extends GuiScreen implements GuiPageButtonList.GuiResponder {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Makriva.MOD_ID, "textures/shapes_menu.png");

    private final int menuWidth = 256;
    private final int menuHeight = 166;
    private int menuX = 0;
    private int menuY = 0;

    private float modelRotation = 0;
    private String selectedShape = Makriva.STORAGE.getCurrentShape().name;

    private ShapeSelectionList shapesList;
    private GuiButton selectBtn;

    @Override
    public void initGui() {
        buttonList.clear();

        menuX = (width - menuWidth) / 2;
        menuY = (height - menuHeight) / 2;

        // Navigation

        shapesList = new ShapeSelectionList(this, mc, 90, 128, menuY + 8, menuY + 136, 12);
        shapesList.setSlotXBoundsFromLeft(menuX + 8);

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
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        shapesList.drawScreen(mouseX, mouseY, partialTicks);
        drawBackground();
        drawModel(mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        final String curr = Makriva.STORAGE.getCurrentShape().name;
        selectBtn.enabled = !curr.equals(selectedShape);
    }

    private void drawBackground() {
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(menuX, menuY, 0, 0, menuWidth, menuHeight);
    }

    private void drawModel(int mouseX, int mouseY) {
        final int viewX = 105;
        final int viewY = 9;
        final int viewWidth = 144;
        final int viewHeight = 128;

        final float modelRealWidth;
        final float modelRealHeight;
        final ModelShape preview = Makriva.MODELS.getPreviewModel();
        if (preview != null) {
            modelRealWidth = preview.shape.getBox(PlayerPose.stand)[0];
            modelRealHeight = preview.shape.getBox(PlayerPose.stand)[1];
        } else {
            final AxisAlignedBB aabb = mc.player.getRenderBoundingBox();
            modelRealWidth = (float) Math.max(aabb.maxX - aabb.minX, aabb.maxZ - aabb.minZ);
            modelRealHeight = (float) (aabb.maxY - aabb.minY);
        }

        final float heightDifference = 1.8f / modelRealHeight;
        final int modelScale = (int) (viewHeight * 0.95 / (2 / heightDifference));
        final int modelWidth = (int) (modelRealWidth * modelScale);

        final int modelX = menuX + viewX + (viewWidth + modelWidth) / 2 - modelWidth / 2;
        final int modelY = menuY + viewY + (int) (viewHeight * 0.95);
        final float modelMouseX = modelX - mouseX;
        final float modelMouseY = modelY - mouseY - mc.player.getEyeHeight() * modelScale;

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
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

    public void selectShape(Shape shape) {
        selectedShape = shape.name;
        Makriva.MODELS.selectPreview(selectedShape);

        selectBtn.enabled = !Makriva.STORAGE.getCurrentShape().name.equals(selectedShape);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        shapesList.handleMouseInput();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        shapesList.mouseClicked(mouseX, mouseY, mouseButton);
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
    }
}
