package net.petercashel.RealmsOfAvalonMod.GUI.Controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonLanguageTextured extends GuiButtonLanguage {
    final ResourceLocation buttonTexture;
    public GuiButtonLanguageTextured(int buttonID, int xPos, int yPos, ResourceLocation texture) {
        super(buttonID, xPos, yPos);
        buttonTexture = texture;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(buttonTexture);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = 106;

            if (flag)
            {
                i += this.height;
            }

            this.drawTexturedModalRect(this.x, this.y, 0, i, this.width, this.height);
        }
    }
}
