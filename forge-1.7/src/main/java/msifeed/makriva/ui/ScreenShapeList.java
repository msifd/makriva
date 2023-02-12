package msifeed.makriva.ui;

import msifeed.makriva.MakrivaShared;
import msifeed.makriva.model.PlayerPose;
import msifeed.makriva.model.Shape;
import msifeed.makriva.render.RenderBridge;
import msifeed.makriva.render.model.ModelShape;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.nio.file.Paths;

public class ScreenShapeList extends GuiScreen {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MakrivaShared.MOD_ID, "textures/shapes_menu.png");

    private final int menuWidth = 256;
    private final int menuHeight = 166;
    private int menuX = 0;
    private int menuY = 0;

    private float modelRotation = 0;
    private Shape selectedShape = MakrivaShared.STORAGE.getCurrentShape();

    private ShapeSelectionList shapesList;
    private GuiButton editBtn;
    private GuiButton selectBtn;

    @Override
    public void initGui() {
        buttonList.clear();

        menuX = (width - menuWidth) / 2;
        menuY = (height - menuHeight) / 2;

        // Navigation

        if (shapesList == null)
            shapesList = new ShapeSelectionList(this, mc, 90, 128, menuY + 8, menuY + 136, 12);
        shapesList.top = menuY + 8;
        shapesList.bottom = menuY + 136;
        shapesList.setSlotXBoundsFromLeft(menuX + 8);

        buttonList.add(new GuiButton(0xa02, menuX + 7, menuY + 139, 51, 20, "Open dir"));

        // Model view

        final GuiSlider rotation = new GuiSlider(this::onRotationChange, 0xc02, menuX + 175, menuY + 139, "", -180, 180, modelRotation, (id, name, value) -> String.format("%.0f", value));
        rotation.width = 74;
        buttonList.add(rotation);

        // Shape controls

        editBtn = new GuiButton(0xb01, menuX + 75, menuY + 139, 46, 20, "Edit");
        editBtn.enabled = false;
        buttonList.add(editBtn);

        selectBtn = new GuiButton(0xb02, menuX + 123, menuY + 139, 50, 20, "Select");
        selectBtn.enabled = false;
        buttonList.add(selectBtn);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        shapesList.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glColor4f(1, 1, 1, 1);

        drawBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        drawModel(mouseX, mouseY);
    }

    @Override
    public void updateScreen() {
        updateButtons();
    }

    private void updateButtons() {
        final ModelShape preview = RenderBridge.getPreviewModel();
        final Shape curr = preview != null
                ? preview.shape
                : MakrivaShared.STORAGE.getCurrentShape();

        selectBtn.enabled = MakrivaShared.STORAGE.getCurrentShape() != selectedShape;
        editBtn.enabled = !curr.internal;
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
        final ModelShape preview = RenderBridge.getPreviewModel();
        if (preview != null) {
            modelRealWidth = preview.shape.getBox(PlayerPose.stand)[0];
            modelRealHeight = preview.shape.getBox(PlayerPose.stand)[1];
        } else {
            final AxisAlignedBB aabb = mc.thePlayer.boundingBox;
            modelRealWidth = (float) Math.max(aabb.maxX - aabb.minX, aabb.maxZ - aabb.minZ);
            modelRealHeight = (float) (aabb.maxY - aabb.minY);
        }

        final AbstractClientPlayer self = mc.thePlayer;
        final float scaleFactorToSteve = 1.8f / modelRealHeight;
        final float modelScale = ((viewHeight * 0.95f) / 2) * scaleFactorToSteve;
        final int modelWidth = (int) (modelRealWidth * modelScale);
        final int modelHeight = (int) (modelRealHeight * modelScale);

        final int modelX = menuX + viewX + (viewWidth + modelWidth) / 2 - modelWidth / 2;
        final int modelY = menuY + viewY + viewHeight + 9 - modelHeight;
        final float modelMouseX = modelX - mouseX;
        final float modelMouseY = modelY - mouseY - self.getEyeHeight() * modelScale;

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1, 1, 1, 1);

        GL11.glPushMatrix();
        GL11.glTranslated(modelX, modelY, 50);
        GL11.glScaled(-modelScale, modelScale, modelScale);
        GL11.glRotated(180, 0, 0, 1);

        float f1 = self.prevRenderYawOffset;
        float f2 = self.prevRotationYawHead;
        float f3 = self.prevRotationPitch;
        float f4 = self.rotationPitch;
        float f5 = self.rotationYaw;

        GL11.glRotated(135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotated(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotated((float) -Math.atan(modelMouseY / 40.0F) * 20, 1, 0, 0);
        self.prevRenderYawOffset = -modelRotation;
        self.prevRotationYawHead = -modelRotation + (float) Math.atan(modelMouseX / 40d) * 40;
        self.prevRotationPitch = (float) -Math.atan(modelMouseY / 40d) * 20;
        self.rotationPitch = (float) -Math.atan(modelMouseY / 40d) * 20;
        self.rotationYaw = self.prevRotationYawHead;

//        manager.setRenderShadow(false);
        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.renderEntityWithPosYaw(self, 0, 0, 0, 0, 0);
//        manager.setRenderShadow(true);

        self.prevRenderYawOffset = f1;
        self.prevRotationYawHead = f2;
        self.prevRotationPitch = f3;
        self.rotationPitch = f4;
        self.rotationYaw = f5;

        GL11.glPopMatrix();

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Override
    public void onGuiClosed() {
        MakrivaShared.MODELS.clearPreview();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0xa02:
                try {
                    Desktop.getDesktop().open(Paths.get(MakrivaShared.MOD_ID).toFile());
                } catch (Throwable e) {
                    MakrivaShared.LOG.error("Can't open makriva dir", e);
                }
                break;
            case 0xb01:
                try {
                    Desktop.getDesktop().open(selectedShape.getShapeFile().toFile());
                } catch (Throwable e) {
                    MakrivaShared.LOG.error("Can't open shape file: " + selectedShape, e);
                }
                break;
            case 0xb02:
                selectBtn.enabled = false;
                MakrivaShared.STORAGE.selectCurrentShape(selectedShape.name);
                break;
        }
    }

    public void selectShape(Shape shape) {
        selectedShape = shape;
        MakrivaShared.MODELS.selectPreview(selectedShape.name);

        updateButtons();
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
//        shapesList.handleMouseInput();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        shapesList.func_148179_a(mouseX, mouseY, mouseButton);
    }

    private void onRotationChange(int id, float value) {
        modelRotation = value;
    }
}
