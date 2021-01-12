package net.petercashel.RealmsOfAvalonMod.GUI.Controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import org.lwjgl.opengl.GL11;

public class GuiButtonExternalTexture extends GuiButton {

    final ExternalTexture texture;

    public GuiButtonExternalTexture(int buttonId, int x, int y, String buttonText, ExternalTexture texture, PositionType positionType)
    {
        this(buttonId, x, y, 200, 20, buttonText, texture);
    }

    public GuiButtonExternalTexture(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, ExternalTexture texture) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.texture = texture;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver)
    {
        int i = 1;

        if (!this.enabled)
        {
            i = 0;
        }
        else if (mouseOver)
        {
            i = 2;
        }

        return i;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            if (!texture.Ready) {
                return;
            }

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);


            GlStateManager.bindTexture(texture.getGlTextureId());

            GL11.glPushMatrix();
            GL11.glTranslatef((float)this.x, (float)this.y, 0.0F);
            GL11.glBegin(7);

            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex3f(0.0F, 0.0F, 0);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex3f(0.0F, (float)this.width, 0);
            GL11.glTexCoord2f(1.0F, 1.0F);
            GL11.glVertex3f((float)this.height, (float)this.height, 0);
            GL11.glTexCoord2f(1.0F, 0.0F);
            GL11.glVertex3f((float)this.width, 0.0F, 0);

            GL11.glEnd();
            GL11.glPopMatrix();


        }
    }

    public void playPressSound(SoundHandler soundHandlerIn)
    {
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
