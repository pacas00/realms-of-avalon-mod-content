package net.petercashel.RealmsOfAvalonMod.GUI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Locale;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.petercashel.RealmsOfAvalonMod.Container.ContainerBasic;
import net.petercashel.RealmsOfAvalonMod.RealmsOfAvalonMod;
import net.petercashel.RealmsOfAvalonMod.TileEntity.Core.TileEntityBase_Container;

public class ContainerBasicGui extends GuiContainer {
    public static final int WIDTH = 180;
    public static final int HEIGHT = 160;

    public boolean extended = false;
    public String windowTitle = "Test Window";

    private static final ResourceLocation background = new ResourceLocation(RealmsOfAvalonMod.MODID, "textures/gui/containerbasic.png");
    private static final ResourceLocation background27 = new ResourceLocation(RealmsOfAvalonMod.MODID, "textures/gui/containerbasic27.png");

    public ContainerBasicGui(TileEntityBase_Container tileEntity, ContainerBasic container) {
        super(container);

        xSize = WIDTH;
        ySize = HEIGHT;

        extended = (tileEntity.getSizeInventory() == 27);

        windowTitle = tileEntity.getDisplayName().getUnformattedText();

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (!extended) {
            mc.getTextureManager().bindTexture(background);
        } else {
            mc.getTextureManager().bindTexture(background27);
        }
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.fontRenderer.drawString(windowTitle, this.xSize / 2 - this.fontRenderer.getStringWidth(windowTitle) / 2, 6, 4210752);
    }
}
