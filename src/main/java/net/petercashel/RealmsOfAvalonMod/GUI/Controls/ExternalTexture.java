package net.petercashel.RealmsOfAvalonMod.GUI.Controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@SideOnly(Side.CLIENT)
public class ExternalTexture extends AbstractTexture
{
    private int[] dynamicTextureData;
    /** width of this icon in pixels */
    public int TextureWidth;
    /** height of this icon in pixels */
    public int TextureHeight;

    public boolean Ready = false;
    public final int X;
    public final int Y;
    public final int RenderWidth;
    public final int RenderHeight;

    public ExternalTexture(int X, int Y, int RenderWidth, int RenderHeight, String Url) {
        this.X = X;
        this.Y = Y;
        this.RenderWidth = RenderWidth;
        this.RenderHeight = RenderHeight;

        try {
            ReplaceTexture(ImageIO.read(new URL(Url)));
        } catch (IOException e) {
            ReplaceTexture(new BufferedImage(1, 1, IndexColorModel.OPAQUE));
        }
        Ready = true;
    }

    public ExternalTexture(int X, int Y, int RenderWidth, int RenderHeight, File file) {
        this.X = X;
        this.Y = Y;
        this.RenderWidth = RenderWidth;
        this.RenderHeight = RenderHeight;

        try {
            ReplaceTexture(ImageIO.read(file));
        } catch (IOException e) {
            ReplaceTexture(new BufferedImage(1, 1, IndexColorModel.OPAQUE));
        }
        Ready = true;
    }

    public ExternalTexture(int X, int Y, int RenderWidth, int RenderHeight, ResourceLocation resource) {
        this(X, Y, RenderWidth, RenderHeight, new File(new File(new File(Minecraft.getMinecraft().mcDataDir, "resources"), resource.getResourceDomain()), resource.getResourcePath()));
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
    }

    public void updateDynamicTexture()
    {
        TextureUtil.uploadTexture(this.getGlTextureId(), this.dynamicTextureData, this.TextureWidth, this.TextureHeight);
    }

    public int[] getTextureData()
    {
        return this.dynamicTextureData;
    }

    private void ReplaceTexture(BufferedImage bufferedImage) {
        int textureWidth = bufferedImage.getWidth();
        int textureHeight = bufferedImage.getHeight();
        this.TextureWidth = textureWidth;
        this.TextureHeight = textureHeight;
        this.dynamicTextureData = new int[textureWidth * textureHeight];
        TextureUtil.allocateTexture(this.getGlTextureId(), textureWidth, textureHeight);
        bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.dynamicTextureData, 0, bufferedImage.getWidth());
        this.updateDynamicTexture();
    }
}