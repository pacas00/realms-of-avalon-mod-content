package net.petercashel.RealmsOfAvalonMod.GUI.Splash;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.petercashel.RealmsOfAvalonMod.GUI.VideoRendering.IVideoFrameDecoder;
import net.petercashel.RealmsOfAvalonMod.GUI.VideoRendering.VideoFrameDecoder;
import net.petercashel.RealmsOfAvalonMod.RealmsOfAvalonModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class GuiSplashScreenPack extends GuiScreen
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final GuiScreen parentScreen;
    /** The text to be displayed when the player's cursor hovers over a server listing. */
    private String hoveringText;
    private boolean initialized;

    private static final ResourceLocation logo = new ResourceLocation("mainmenu:textures/splashlogo.png");
    private static final ResourceLocation bg = new ResourceLocation("mainmenu:textures/splashbackground.png");
    private static final ResourceLocation bgvideo = new ResourceLocation("mainmenu:textures/splashbackgroundvideo.mp4");
    private static final ResourceLocation cloud = new ResourceLocation("mainmenu:textures/cloud.png");
    private static final Random random = new Random();
    private int tickCount = 0;
    private int tickCountText = 0;
    private List<CloudPos> clouds = new ArrayList<CloudPos>();

    private boolean backgroundExists = false;
    private boolean videoFileExists = false;
    private File videoFile = null;

    IVideoFrameDecoder videoFrameDecoder = null;

    public GuiSplashScreenPack(GuiScreen parentScreen)
    {
        this.parentScreen = parentScreen;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        if (this.initialized)
        {

        }
        else
        {
            this.initialized = true;
            this.mc.getSoundHandler().stopSounds();

            backgroundExists = new File(new File(new File(this.mc.mcDataDir, "resources"), bg.getResourceDomain()), bg.getResourcePath()).exists();
            videoFile = new File(new File(new File(this.mc.mcDataDir, "resources"), bgvideo.getResourceDomain()), bgvideo.getResourcePath());
            videoFileExists = videoFile.exists();

            //Check if a video exists, if so, load some info
            if (videoFileExists) {
                //LOAD DATA
                videoFrameDecoder = new VideoFrameDecoder(videoFile, glGenTextures());
                videoFrameDecoder.Setup();
                videoFrameDecoder.StartThread();
            }

            //If we shouldn't force the static background to stay on, and we have a video background, disable static
            if (backgroundExists && !RealmsOfAvalonModConfig.splashBackgroundForceEnabled) {
                if (videoFileExists && RealmsOfAvalonModConfig.splashVideoEnabled) {
                    backgroundExists = false;
                }
            }
        }

        this.createButtons();
    }



    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

    }

    public void createButtons()
    {
        //this.buttonList.add(new GuiButton(0, this.width / 2 + 4 - 50, this.height - 28, 100, 20, "Start"));

    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        Minecraft.getMinecraft().getSoundHandler().stopSounds();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        videoFrameDecoder.StopThread();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 0)
            {
                this.TriggerClose();
            }
        }
    }


    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        this.TriggerClose();
    }

    private void TriggerClose() {
        videoFrameDecoder.StopThread();
        this.mc.displayGuiScreen(this.parentScreen);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        tickCount++;
        if (tickCount >= 3) {
            tickCount = 0;
        }

        tickCountText++;
        if (tickCountText >= 100) {
            tickCountText = 0;
        }

        int wCenter = this.width / 2;
        int hCenter = this.height / 2;

        this.hoveringText = null;

        if (videoFileExists && RealmsOfAvalonModConfig.splashVideoEnabled) {
            this.drawVideoFrame();
        }

        if (backgroundExists) {
            this.drawBackground(0, wCenter, hCenter);
        }

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));

        //Populate clouds if they are not
        if (clouds.size() == 0) {
            for (int i = 0; i <= 4; i++) {
                clouds.add(new CloudPos((0 - (random.nextInt(32))) + (i * 120), random.nextInt(this.height / 5)));
            }
        }

        //If we are short on clouds, generate another
        if (clouds.size() <= 4) {
            if (clouds.size() == 0 || (clouds.get(clouds.size() - 1).x > 100)) {
                CloudPos c = new CloudPos(0 - (105 + 8), random.nextInt(this.height / 5));
                clouds.add(0, c);
            }
        }

        //tick and render clouds
        for (int i = 0; i < clouds.size(); i++) {
            CloudPos pos = clouds.get(i);
            if (tickCount % 3 == 1) {
                pos.tick();
            }
            clouds.set(i, pos);

            drawCompleteImage(pos.x, pos.y, 105, 40, cloud, false,true);
        }

        //remove oob clouds
        for (int i = clouds.size() - 1; i >= 0; i--) {
            CloudPos pos = clouds.get(i);
            if (pos.x >= this.width) {
                clouds.remove(i);
            }
        }

        //render logo
        int logoX = wCenter - (619 / 4);
        int logoY = hCenter - (84 / 4);
        drawCompleteImage(logoX , logoY - (this.height / 4), 619 / 2, 84 / 2, logo, false, true);


        //Pretty sure something in either the colours or the math is wrong below here, but it's fine for now.

        //calculate colour
        float lerpAmount = 1.0f;

        if (tickCountText < 50) {
            lerpAmount = tickCountText / 50f;
        } else if (tickCountText > 50) {
            lerpAmount = (50f - (tickCountText - 50f)) / 50f;
        } else if (tickCountText == 50) {
            lerpAmount = 1f;
        }

        int colour = LerpTowardsColour(16777215, 128, 128, 128, lerpAmount);
        this.drawCenteredString(this.fontRenderer, "Press any key or click", this.width / 2, this.height - (this.height / 4), colour);

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.hoveringText != null)
        {
            this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
        }

        if (RealmsOfAvalonModConfig.splashVideoShowFPS) {
            if (videoFrameDecoder.FPSAVG() > 25) {
                this.drawCenteredString(this.fontRenderer, "" + videoFrameDecoder.FPSAVG(), this.width - 8, 2, 16777215);
            } else {
                this.drawCenteredString(this.fontRenderer, "" + videoFrameDecoder.FPSAVG(), this.width - 8, 2, 8327184);
            }
        }
    }

    private int LerpTowardsColour(int source, int r, int g, int b, float f) {
        //Unpack
        float alpha = (float)(source >> 24 & 255) / 255.0F;
        float red = (float)(source >> 16 & 255) / 255.0F;
        float blue = (float)(source >> 8 & 255) / 255.0F;
        float green = (float)(source & 255) / 255.0F;

        //LERP
        red = red * (1.0f - f) + (r / 255) * f;
        green = green * (1.0f - f) + (g / 255) * f;
        blue = blue * (1.0f - f) + (b / 255) * f;

        int outA = ((int) (alpha * 255F)) << 24;
        int outR = ((int) (red * 255F)) << 16;
        int outB = ((int) (blue * 255F)) << 8;
        int outG = ((int) (green * 255F));

        return outA + outB + outG + outR;
    }

    /**
     * Draws the background (i is always 0 as of 1.2.2)
     */
    public void drawBackground(int tint, int wCenter, int hCenter)
    {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        drawCompleteImage(0,0, this.width, this.height, bg, false, RealmsOfAvalonModConfig.splashBackgroundBlendEnabled);
    }

    public void drawCompleteImage(int posX, int posY, int width, int height, ResourceLocation texture, boolean isTransparent, boolean isBlend) {
        drawCompleteImage((float) posX, (float) posY, width, height, texture, isTransparent, isBlend);
    }

    public void drawCompleteImage(float posX, float posY, int width, int height, ResourceLocation texture, boolean isTransparent, boolean isBlend) {
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
        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(0.0F, (float)height, 0.0F);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f((float)width, (float)height, 0.0F);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f((float)width, 0.0F, 0.0F);


        GL11.glEnd();
        GL11.glPopMatrix();


        if (isBlend) {
            glDisable(GL_BLEND);
        }

        if (isTransparent) {
            GlStateManager.disableAlpha();
        }
    }


    public void drawVideoFrame() {

        GlStateManager.disableLighting();
        GlStateManager.disableFog();

        videoFrameDecoder.BindNextFrame();
        GlStateManager.bindTexture(videoFrameDecoder.currTextureID);

        GL11.glPushMatrix();
        GL11.glTranslatef((float)0, (float)0, 0.0F);
        GL11.glBegin(7);

        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(0.0F, (float)height, 0.0F);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f((float)width, (float)height, 0.0F);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f((float)width, 0.0F, 0.0F);


        GL11.glEnd();
        GL11.glPopMatrix();

    }

    public void setHoveringText(String p_146793_1_)
    {
        this.hoveringText = p_146793_1_;
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.TriggerClose();

    }

    /**
     * Called when a mouse button is released.
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
    }


}