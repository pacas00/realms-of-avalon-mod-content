package net.petercashel.RealmsOfAvalonMod.GUI;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.petercashel.RealmsOfAvalonMod.GUI.Controls.PositionType;
import net.petercashel.RealmsOfAvalonMod.GUI.Controls.ExternalTexture;
import net.petercashel.RealmsOfAvalonMod.GUI.Splash.MovingTexture;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;

public class GuiScreenPack extends GuiScreen {


    //Ctrl Mac, Ctrl Mac, Ctrl, Ctrl, Alt, Alt, Shift, Shift
    public int[] specialKeys = new int[] {219, 220, 29, 157, 56, 184, 42, 54, Keyboard.KEY_F11};

    public static boolean isKeyComboCtrl(int keyID, int comboKeyID)
    {
        return keyID == comboKeyID && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    /**
     * Draws the background (i is always 0 as of 1.2.2)
     */
    public void drawBackground(int tint, int wCenter, int hCenter, ResourceLocation texture, boolean blend, int depth)
    {
        drawCompleteImage(0,0, this.width, this.height, texture, false, blend, depth);
    }

    public void drawCompleteImage(int posX, int posY, int width, int height, ResourceLocation texture, boolean isTransparent, boolean isBlend, int depth) {
        drawCompleteImage((float) posX, (float) posY, width, height, texture, isTransparent, isBlend, depth);
    }

    public void drawMovingTexture(float posX, float posY, MovingTexture movingTexture, boolean isTransparent, boolean isBlend, int depth) {
        drawCompleteImage((float) posX, (float) posY, movingTexture.width, movingTexture.height, movingTexture.texture, isTransparent, isBlend, depth);
    }

    public void drawCompleteImage(float posX, float posY, int width, int height, ResourceLocation texture, boolean isTransparent, boolean isBlend, int depth) {
        if (isTransparent) {
            GlStateManager.enableAlpha();
        }
        if (isBlend) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }

        this.mc.getTextureManager().bindTexture(texture);

        GL11.glPushMatrix();
        GL11.glTranslatef((float)posX, (float)posY, 0.0F);
        GL11.glBegin(7);

        if (isTransparent) {
            GlStateManager.color(0.97f,0.97f,0.97f,0.5f);
        }

        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(0.0F, 0.0F, depth);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(0.0F, (float)height, depth);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f((float)width, (float)height, depth);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f((float)width, 0.0F, depth);

        GL11.glEnd();
        GL11.glPopMatrix();

        if (isBlend) {
            glDisable(GL_BLEND);
        }

        if (isTransparent) {
            GlStateManager.disableAlpha();
        }
    }


    public int TransformX(int x, PositionType type) {
        if (type == PositionType.TOP_RIGHT || type == PositionType.BOTTOM_RIGHT) {
            return this.width + x;
        }
        if (type == PositionType.TOP_CENTER || type == PositionType.BOTTOM_CENTER) {
            return (this.width / 2) + 1 + x;
        }
        return x;
    }

    public int TransformY(int y, PositionType type) {
        if (type == PositionType.BOTTOM_LEFT || type == PositionType.BOTTOM_RIGHT || type == PositionType.BOTTOM_CENTER) {
            return this.height + y;
        }
        return y;
    }


    protected void drawExternalTexture(ExternalTexture texture, boolean isTransparent, boolean isBlend, int depth) {
        if (!texture.Ready) {
            return;
        }

        if (isTransparent) {
            GlStateManager.enableAlpha();
        }
        if (isBlend) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }

        GlStateManager.bindTexture(texture.getGlTextureId());

        GL11.glPushMatrix();
        GL11.glTranslatef((float)texture.X, (float)texture.Y, 0.0F);
        GL11.glBegin(7);

        if (isTransparent) {
            GlStateManager.color(0.97f,0.97f,0.97f,0.5f);
        }

        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(0.0F, 0.0F, depth);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(0.0F, (float)texture.RenderHeight, depth);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f((float)texture.RenderWidth, (float)texture.RenderHeight, depth);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f((float)texture.RenderWidth, 0.0F, depth);

        GL11.glEnd();
        GL11.glPopMatrix();

        if (isBlend) {
            glDisable(GL_BLEND);
        }

        if (isTransparent) {
            GlStateManager.disableAlpha();
        }
    }

    /**
     * Renders the specified text to the screen. Args : renderer, string, x, y, color
     */
    public void drawStringScaled(FontRenderer fontRendererIn, String text, int x, int y, int color, float scale)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, 0.0F);
        GlStateManager.scale(scale, scale, 1);
        fontRendererIn.drawStringWithShadow(text, (float)0, (float)0, color);
        GlStateManager.scale(1, 1, 1);
        GL11.glPopMatrix();
    }
}
