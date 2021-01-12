package net.petercashel.RealmsOfAvalonMod.GUI.Splash;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.petercashel.RealmsOfAvalonMod.GUI.Controls.Textures;
import net.petercashel.RealmsOfAvalonMod.GUI.GuiScreenPack;
import net.petercashel.RealmsOfAvalonMod.GUI.MainMenu.GuiMainMenuPack;
import net.petercashel.RealmsOfAvalonMod.GUI.VideoRendering.IVideoFrameDecoder;
import net.petercashel.RealmsOfAvalonMod.GUI.VideoRendering.VideoFrameDecoder;
import net.petercashel.RealmsOfAvalonMod.RealmsOfAvalonModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glGenTextures;

@SideOnly(Side.CLIENT)
public class GuiSplashScreenPack extends GuiScreenPack
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final GuiScreen parentScreen;
    /** The text to be displayed when the player's cursor hovers over a server listing. */
    private String hoveringText;
    private boolean initialized;

    private static final Random random = new Random();
    private int tickCount = 0;
    private int tickCountText = 0;
    private List<MovingTexture> clouds = new ArrayList<MovingTexture>();
    private List<MovingTexture> fogs = new ArrayList<MovingTexture>();

    private boolean backgroundExists = false;
    private boolean videoFileExists = false;
    private boolean showVideo = true;
    private File videoFile = null;
    private int videoLoadingImageTexID = 0;
    private boolean DontDisposeOfVideo = false;

    IVideoFrameDecoder videoFrameDecoder = null;



    private boolean showFog = false;

    public GuiSplashScreenPack(GuiScreen parentScreen)
    {
        this.parentScreen = parentScreen;
    }

    public GuiSplashScreenPack(GuiMainMenuPack mainMenu, boolean backgroundExists, boolean videoFileExists, boolean showVideo, File videoFile, IVideoFrameDecoder videoFrameDecoder, int videoLoadingImageTexID)
    {
        this.parentScreen = mainMenu;
        DontDisposeOfVideo = true;
        this.initialized = true;

        this.backgroundExists = backgroundExists;
        this.videoFileExists = videoFileExists;
        this.showVideo = showVideo;
        this.videoFile = videoFile;
        this.videoFrameDecoder = videoFrameDecoder;
        this.videoLoadingImageTexID = videoLoadingImageTexID;

        //videoFrameDecoder.StartThread();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        if (!showFog) {
            if (random.nextInt(1000) < 10) {
                showFog = true;
            }
        }

        if (this.initialized)
        {

        }
        else
        {
            this.initialized = true;
            this.mc.getSoundHandler().stopSounds();

            backgroundExists = new File(new File(new File(this.mc.mcDataDir, "resources"), Textures.splashbg.getResourceDomain()), Textures.splashbg.getResourcePath()).exists();
            videoFile = new File(new File(new File(this.mc.mcDataDir, "resources"), Textures.splashbgvideo.getResourceDomain()), Textures.splashbgvideo.getResourcePath());
            videoFileExists = videoFile.exists();

            //Check if a video exists, if so, load some info
            if (videoFileExists) {
                //LOAD DATA
                videoFrameDecoder = new VideoFrameDecoder(videoFile, glGenTextures());
                videoLoadingImageTexID = videoFrameDecoder.Setup(glGenTextures());
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
        if (!DontDisposeOfVideo) videoFrameDecoder.StopThread();
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
        if (isKeyComboCtrl(keyCode, Keyboard.KEY_L)) {
            this.videoFrameDecoder.ToggleFPSLock();
        } else if (isKeyComboCtrl(keyCode, Keyboard.KEY_P)) {
            RealmsOfAvalonModConfig.splashVideoShowFPS = !RealmsOfAvalonModConfig.splashVideoShowFPS;
        } else if (isKeyComboCtrl(keyCode, Keyboard.KEY_APOSTROPHE)) {
            this.backgroundExists = !this.backgroundExists;
        } else if (isKeyComboCtrl(keyCode, Keyboard.KEY_SEMICOLON)) {
            this.showVideo = !this.showVideo;
        } else {
            if (!Arrays.stream(specialKeys).anyMatch(x -> x == keyCode)) {
                this.TriggerClose();
            } else {
                super.keyTyped(typedChar, keyCode);
            }
        }
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

    private void TriggerClose() {
        videoFrameDecoder.StopThread();

        if (this.parentScreen instanceof GuiMainMenuPack) {
            //WOOT! TRANSFER INSTANCES
            ((GuiMainMenuPack)this.parentScreen).SetInstances(showFog, clouds, fogs);
        }

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

        if (videoFileExists && showVideo && RealmsOfAvalonModConfig.splashVideoEnabled) {
            this.drawVideoFrame(true, -1);
        }

        if (backgroundExists) {
            this.drawBackground(0, wCenter, hCenter, Textures.splashbg, RealmsOfAvalonModConfig.splashBackgroundBlendEnabled, 0);
        }

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));

        if (!showFog) {
            //Populate clouds if they are not
            if (clouds.size() == 0) {
                for (int i = 0; i <= 4; i++) {
                    clouds.add(new MovingTexture((0 - (random.nextInt(32))) + (i * 120), random.nextInt(this.height / 5), 105, 40, Textures.cloud));
                }
            }

            //If we are short on clouds, generate another
            if (clouds.size() <= 4) {
                if (clouds.size() == 0 || (clouds.get(clouds.size() - 1).x > 100)) {
                    MovingTexture c = new MovingTexture(0 - (105 + 8), random.nextInt(this.height / 5), 105, 40, Textures.cloud);
                    clouds.add(0, c);
                }
            }

            //tick and render clouds
            for (int i = 0; i < clouds.size(); i++) {
                MovingTexture pos = clouds.get(i);
                if (tickCount % 3 == 1) {
                    pos.tick();
                }
                clouds.set(i, pos);

                drawMovingTexture(pos.x, pos.y, clouds.get(i), false, true, 0);
            }

            //remove oob clouds
            for (int i = clouds.size() - 1; i >= 0; i--) {
                MovingTexture pos = clouds.get(i);
                if (pos.x >= this.width) {
                    clouds.remove(i);
                }
            }
        }

        if (showFog) {
            drawCompleteImage(0 , 0, width, height, Textures.fog, false, true, 0);

            //Populate fogs if they are not
            if (fogs.size() == 0) {
                for (int i = 0; i <= 5; i++) {
                    MovingTexture newFog = new MovingTexture((0 - (random.nextInt(100))), 0, (this.width + 220), height + (height / 4) + random.nextInt(height / 4), Textures.fog);
                    newFog.BounceMovement = true;
                    newFog.MoveRight = random.nextBoolean();
                    newFog.MovementSpeed = 0.015f;
                    fogs.add(newFog);
                }
            }

            //tick and render fogs
            for (int i = 0; i < fogs.size(); i++) {
                MovingTexture pos = fogs.get(i);
                if (tickCount % 3 == 1) {
                    pos.tickWithRandomSpeedBonux(0.25f);
                }
                fogs.set(i, pos);

                drawMovingTexture(pos.x, pos.y, fogs.get(i), false, true, 0);
            }

        }


        //render logo
        int logoX = wCenter - (619 / 4);
        int logoY = hCenter - (84 / 4);
        drawCompleteImage(logoX , logoY - (this.height / 4), 619 / 2, 84 / 2, Textures.splashlogo, false, true, 0);


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
            if (videoFrameDecoder.FPSAVG() >= 25) {
                this.drawCenteredString(this.fontRenderer, "" + videoFrameDecoder.FPSAVG(), this.width - 8, 2, 16777215); //White text
            } else {
                this.drawCenteredString(this.fontRenderer, "" + videoFrameDecoder.FPSAVG(), this.width - 8, 2, 8327184); //Red Text, less than 25 fps for decode
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

    public void drawVideoFrame(boolean isBlend, int depth) {

        if (isBlend) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }

        boolean bindResult = videoFrameDecoder.BindNextFrame();
        if (bindResult) {
            GlStateManager.bindTexture(videoFrameDecoder.currTextureID);
        } else {
            GlStateManager.bindTexture(this.videoLoadingImageTexID);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float)0, (float)0, 0.0F);
        GL11.glBegin(7);

        GL11.glTexCoord3f(0.0F, 0.0F, depth);
        GL11.glVertex3f(0.0F, 0.0F, depth);
        GL11.glTexCoord3f(0.0F, 1.0F, depth);
        GL11.glVertex3f(0.0F, (float)height, depth);
        GL11.glTexCoord3f(1.0F, 1.0F, depth);
        GL11.glVertex3f((float)width, (float)height, depth);
        GL11.glTexCoord3f(1.0F, 0.0F, depth);
        GL11.glVertex3f((float)width, 0.0F, depth);


        GL11.glEnd();
        GL11.glPopMatrix();

        if (isBlend) {
            glDisable(GL_BLEND);
        }
    }

    public void setHoveringText(String p_146793_1_)
    {
        this.hoveringText = p_146793_1_;
    }


}