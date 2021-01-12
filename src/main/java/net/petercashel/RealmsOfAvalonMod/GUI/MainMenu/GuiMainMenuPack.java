package net.petercashel.RealmsOfAvalonMod.GUI.MainMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;
import net.petercashel.RealmsOfAvalonMod.GUI.Controls.*;
import net.petercashel.RealmsOfAvalonMod.GUI.GuiScreenPack;
import net.petercashel.RealmsOfAvalonMod.GUI.Servers.GuiMultiplayerPack;
import net.petercashel.RealmsOfAvalonMod.GUI.Splash.GuiSplashScreenPack;
import net.petercashel.RealmsOfAvalonMod.GUI.Splash.MovingTexture;
import net.petercashel.RealmsOfAvalonMod.GUI.VideoRendering.IVideoFrameDecoder;
import net.petercashel.RealmsOfAvalonMod.GUI.VideoRendering.VideoFrameDecoder;
import net.petercashel.RealmsOfAvalonMod.RealmsOfAvalonModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class GuiMainMenuPack extends GuiScreenPack {

    private int widthCopyright;
    private int widthCopyrightRest;

    public GuiMainMenuPack() {
        this.mc = Minecraft.getMinecraft();
    }

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random random = new Random();




    private int tickCount = 0;
    private int tickCountText = 0;
    private List<MovingTexture> clouds = new ArrayList<MovingTexture>();
    private static List<MovingTexture> fogs = new ArrayList<MovingTexture>();
    private static boolean showFog = false;

    private static boolean launchedSplashScreen = false;

    private boolean initialized;
    private boolean isLaunchingSplash = false;

    private boolean backgroundExists = false;
    private boolean videoFileExists = false;
    private boolean showVideo = true;
    private File videoFile = null;
    private int videoLoadingImageTexID = 0;

    IVideoFrameDecoder videoFrameDecoder = null;

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        isLaunchingSplash = false;

        if (this.initialized)
        {
            videoFrameDecoder.StartThread();
        }
        else
        {
            this.initialized = true;
            this.PrepareFiles();
            this.createVideoDecoder();
        }

        this.createButtons();

        this.widthCopyright = this.fontRenderer.getStringWidth("Copyright Mojang AB. Do not distribute!") / 2;
        this.widthCopyrightRest = this.width - this.widthCopyright - 2;


        if (launchedSplashScreen == false && RealmsOfAvalonModConfig.splashEnabled) {
            launchedSplashScreen = true;
            isLaunchingSplash = true;

            if (RealmsOfAvalonModConfig.customMainMenuUsesSplashBackgrounds) {
                GuiSplashScreenPack splash = new GuiSplashScreenPack(this, backgroundExists, videoFileExists, showVideo, videoFile, videoFrameDecoder, videoLoadingImageTexID);
                Minecraft.getMinecraft().displayGuiScreen(splash);
            } else {
                GuiSplashScreenPack splash = new GuiSplashScreenPack(this);
                Minecraft.getMinecraft().displayGuiScreen(splash);
            }
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        if (!isLaunchingSplash) videoFrameDecoder.StopThread();
    }

    private void PrepareFiles() {
        if (RealmsOfAvalonModConfig.customMainMenuUsesSplashBackgrounds) {
            backgroundExists = new File(new File(new File(this.mc.mcDataDir, "resources"), Textures.splashbg.getResourceDomain()), Textures.splashbg.getResourcePath()).exists();
            videoFile = new File(new File(new File(this.mc.mcDataDir, "resources"), Textures.splashbgvideo.getResourceDomain()), Textures.splashbgvideo.getResourcePath());
            videoFileExists = videoFile.exists();
        } else {
            backgroundExists = new File(new File(new File(this.mc.mcDataDir, "resources"), Textures.menubg.getResourceDomain()), Textures.menubg.getResourcePath()).exists();
            videoFile = new File(new File(new File(this.mc.mcDataDir, "resources"), Textures.menubgvideo.getResourceDomain()), Textures.menubgvideo.getResourcePath());
            videoFileExists = videoFile.exists();
        }
    }

    private void createVideoDecoder() {
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

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
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
        } else if (keyCode == Keyboard.KEY_ESCAPE) {
            GuiSplashScreenPack pack = new GuiSplashScreenPack(this);
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)pack);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    private GuiButton modButton;
    private net.minecraftforge.client.gui.NotificationModUpdateScreen modUpdateNotification;

    private ExternalTexture McUserFace;
    public void createButtons()
    {
        int xCenter = this.width / 2;
        int yCenter = this.height / 2;

        int ButtonY = 24;
        int ButtonYStart = this.height / 4 + 48;
        //Left
        this.buttonList.add(new GuiButtonTextured(1, -4, 60, 100, 20, I18n.format("menu.singleplayer"), Textures.menubuttons));
        this.buttonList.add(new GuiButtonTextured(2, -4, 80, 100, 20, I18n.format("menu.multiplayer"), Textures.menubuttons));
        //Server List
        this.buttonList.add(new GuiButtonTextured(7001, -4, 120, 100, 20, I18n.format("gui.realmsofavalonmod.serverbutton"), Textures.menubuttons));

        //Right
        this.buttonList.add(modButton = new GuiButtonTextured(6, TransformX(-96, PositionType.TOP_RIGHT), TransformY(60, PositionType.TOP_RIGHT), 100, 20, I18n.format("fml.menu.mods"), Textures.menubuttons));
        NotificationModUpdateScreen notificationModUpdateScreen = new NotificationModUpdateScreen(modButton);
        notificationModUpdateScreen.setGuiSize(this.width, this.height);
        notificationModUpdateScreen.initGui();
        modUpdateNotification = notificationModUpdateScreen;

        this.buttonList.add(new GuiButtonTextured(0, TransformX(-96, PositionType.TOP_RIGHT), TransformY(80, PositionType.TOP_RIGHT), 100, 20, I18n.format("menu.options"), Textures.menubuttons));

        //SOCAL GOES HERE
        this.buttonList.add(new GuiButtonTextured(7003, TransformX(-96, PositionType.TOP_RIGHT), TransformY(120, PositionType.TOP_RIGHT), 100, 20, I18n.format("gui.realmsofavalonmod.socialbutton"), Textures.menubuttons));

        //Bottom Center
        this.buttonList.add(new GuiButtonTextured(4, TransformX(-50, PositionType.BOTTOM_CENTER), TransformY(-20, PositionType.BOTTOM_CENTER), 100, 20, I18n.format("menu.quit"), Textures.menubuttons));
        this.buttonList.add(new GuiButtonLanguageTextured(5, TransformX(-74, PositionType.BOTTOM_CENTER), TransformY(-20, PositionType.BOTTOM_CENTER), Textures.menubuttons));

        //Player Face
        McUserFace = new ExternalTexture(TransformX(54, PositionType.BOTTOM_CENTER), TransformY(-20, PositionType.BOTTOM_CENTER), 20, 20, "https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/20");

        //Primary Streamers
        this.buttonList.add(new GuiButtonExternalTexture(40, TransformX(20, PositionType.TOP_LEFT), TransformY(2, PositionType.TOP_LEFT), 48,48, "", new ExternalTexture(0, 0, 48,48, Textures.PlumYeti)));
        this.buttonList.add(new GuiButtonExternalTexture(41, TransformX(-64, PositionType.TOP_RIGHT), TransformY(6, PositionType.TOP_RIGHT), 48,48, "", new ExternalTexture(0, 0, 48,48, Textures.NerdiCorgi_Tadakichi)));


        //Social View, Offset 1000

        //Plum Yeti
        this.buttonList.add(new GuiButtonTextured(5001, -1004, 60, 100, 20, "Twitch", Textures.menubuttons));
        this.buttonList.add(new GuiButtonTextured(5002, -1004, 80, 100, 20, "Twitter", Textures.menubuttons));
        this.buttonList.add(new GuiButtonTextured(5003, -1004, 100, 100, 20, "Discord", Textures.menubuttons));


        this.buttonList.add(new GuiButtonTextured(5004, TransformX(-96 + 1000, PositionType.TOP_RIGHT), TransformY(60, PositionType.TOP_RIGHT), 100, 20, "Twitch", Textures.menubuttons));
        this.buttonList.add(new GuiButtonTextured(5005, TransformX(-96 + 1000, PositionType.TOP_RIGHT), TransformY(80, PositionType.TOP_RIGHT), 100, 20, "Twitter", Textures.menubuttons));
        this.buttonList.add(new GuiButtonTextured(5006, TransformX(-96 + 1000, PositionType.TOP_RIGHT), TransformY(100, PositionType.TOP_RIGHT), 100, 20, "Discord", Textures.menubuttons));

        //Bottom Center
        this.buttonList.add(new GuiButtonTextured(5007, TransformX(-50 + 1000, PositionType.BOTTOM_CENTER), TransformY(-20, PositionType.BOTTOM_CENTER), 100, 20, "Back", Textures.menubuttons));
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
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
            }
            if (button.id == 5)
            {
                this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
            }

            if (button.id == 1)
            {
                this.mc.displayGuiScreen(new GuiWorldSelection(this));
            }

            if (button.id == 2)
            {
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
            }

            if (button.id == 4)
            {
                this.mc.shutdown();
            }

            if (button.id == 6)
            {
                this.mc.displayGuiScreen(new net.minecraftforge.fml.client.GuiModList(this));
            }

            if (button.id == 7001 && RealmsOfAvalonModConfig.serverListEnabled) //Hopefully it's our custom main menu button.
            {
                GuiMultiplayerPack pack = new GuiMultiplayerPack(this);
                Minecraft.getMinecraft().displayGuiScreen((GuiScreen)pack);
            }

            //Social
            if (button.id == 7003 && RealmsOfAvalonModConfig.customMainMenuEnabled) //Hopefully it's our custom main menu button.
            {
                //Shift Button Set Main out
                //SP
                GuiButton moveButton = this.buttonList.stream().filter(x -> x.id == 1).findFirst().get();
                moveButton.x += -1000;
                //MP
                moveButton = this.buttonList.stream().filter(x -> x.id == 2).findFirst().get();
                moveButton.x += -1000;
                //OPS
                moveButton = this.buttonList.stream().filter(x -> x.id == 7001).findFirst().get();
                moveButton.x += -1000;

                //Mods
                moveButton = this.buttonList.stream().filter(x -> x.id == 6).findFirst().get();
                moveButton.x += 1000;
                //OPTS
                moveButton = this.buttonList.stream().filter(x -> x.id == 0).findFirst().get();
                moveButton.x += 1000;
                //Social
                moveButton = this.buttonList.stream().filter(x -> x.id == 7003).findFirst().get();
                moveButton.x += 1000;
                //Quit
                moveButton = this.buttonList.stream().filter(x -> x.id == 4).findFirst().get();
                moveButton.x += 1000;


                //Shift Button Set Social In
                //Plum Twitch
                moveButton = this.buttonList.stream().filter(x -> x.id == 5001).findFirst().get();
                moveButton.x += 1000;
                //Plum Twitter
                moveButton = this.buttonList.stream().filter(x -> x.id == 5002).findFirst().get();
                moveButton.x += 1000;
                //Plum Discord
                moveButton = this.buttonList.stream().filter(x -> x.id == 5003).findFirst().get();
                moveButton.x += 1000;

                //Nerdi Twitch
                moveButton = this.buttonList.stream().filter(x -> x.id == 5004).findFirst().get();
                moveButton.x += -1000;
                //Twitter
                moveButton = this.buttonList.stream().filter(x -> x.id == 5005).findFirst().get();
                moveButton.x += -1000;
                //Discord
                moveButton = this.buttonList.stream().filter(x -> x.id == 5006).findFirst().get();
                moveButton.x += -1000;
                //Back
                moveButton = this.buttonList.stream().filter(x -> x.id == 5007).findFirst().get();
                moveButton.x += -1000;
            }

            if (button.id == 5007 && RealmsOfAvalonModConfig.customMainMenuEnabled) //Hopefully it's our custom main menu button.
            {
                //Shift Button Set Main in
                //SP
                GuiButton moveButton = this.buttonList.stream().filter(x -> x.id == 1).findFirst().get();
                moveButton.x += 1000;
                //MP
                moveButton = this.buttonList.stream().filter(x -> x.id == 2).findFirst().get();
                moveButton.x += 1000;
                //OPS
                moveButton = this.buttonList.stream().filter(x -> x.id == 7001).findFirst().get();
                moveButton.x += 1000;

                //Mods
                moveButton = this.buttonList.stream().filter(x -> x.id == 6).findFirst().get();
                moveButton.x += -1000;
                //OPTS
                moveButton = this.buttonList.stream().filter(x -> x.id == 0).findFirst().get();
                moveButton.x += -1000;
                //Social
                moveButton = this.buttonList.stream().filter(x -> x.id == 7003).findFirst().get();
                moveButton.x += -1000;
                //Quit
                moveButton = this.buttonList.stream().filter(x -> x.id == 4).findFirst().get();
                moveButton.x += -1000;


                //Shift Button Set Social out
                //Plum Twitch
                moveButton = this.buttonList.stream().filter(x -> x.id == 5001).findFirst().get();
                moveButton.x += -1000;
                //Plum Twitter
                moveButton = this.buttonList.stream().filter(x -> x.id == 5002).findFirst().get();
                moveButton.x += -1000;
                //Plum Discord
                moveButton = this.buttonList.stream().filter(x -> x.id == 5003).findFirst().get();
                moveButton.x += -1000;

                //Nerdi Twitch
                moveButton = this.buttonList.stream().filter(x -> x.id == 5004).findFirst().get();
                moveButton.x += 1000;
                //Twitter
                moveButton = this.buttonList.stream().filter(x -> x.id == 5005).findFirst().get();
                moveButton.x += 1000;
                //Discord
                moveButton = this.buttonList.stream().filter(x -> x.id == 5006).findFirst().get();
                moveButton.x += 1000;
                //Back
                moveButton = this.buttonList.stream().filter(x -> x.id == 5007).findFirst().get();
                moveButton.x += 1000;
            }

            //40 Plum
            if (button.id == 40)
            {
                beingCheckedLink = "https://www.twitch.tv/PlumYeti";
                GuiConfirmOpenLink gui = new GuiConfirmOpenLink(this, beingCheckedLink, 40, false);
                beingChecked = button;
                Minecraft.getMinecraft().displayGuiScreen(gui);
            }

            //41 Nerdi
            if (button.id == 41)
            {
                beingCheckedLink = "https://www.twitch.tv/nerdicorgi";
                GuiConfirmOpenLink gui = new GuiConfirmOpenLink(this, beingCheckedLink, 40, false);
                beingChecked = button;
                Minecraft.getMinecraft().displayGuiScreen(gui);
            }

            //Plum Twitch
            if (button.id == 5001)
            {
                beingCheckedLink = "https://www.twitch.tv/PlumYeti";
                GuiConfirmOpenLink gui = new GuiConfirmOpenLink(this, beingCheckedLink, 5001, false);
                beingChecked = button;
                Minecraft.getMinecraft().displayGuiScreen(gui);
            }

            //Plum Twitter
            if (button.id == 5002)
            {
                beingCheckedLink = "https://twitter.com/PlumYeti";
                GuiConfirmOpenLink gui = new GuiConfirmOpenLink(this, beingCheckedLink, 5002, false);
                beingChecked = button;
                Minecraft.getMinecraft().displayGuiScreen(gui);
            }

            //Plum Discord
            if (button.id == 5003)
            {
                beingCheckedLink = "https://discord.gg/6bxA5TY";
                GuiConfirmOpenLink gui = new GuiConfirmOpenLink(this, beingCheckedLink, 5003, false);
                beingChecked = button;
                Minecraft.getMinecraft().displayGuiScreen(gui);
            }

            //NerdiCorgi Twitch
            if (button.id == 5004)
            {
                beingCheckedLink = "https://www.twitch.tv/nerdicorgi";
                GuiConfirmOpenLink gui = new GuiConfirmOpenLink(this, beingCheckedLink, 5004, false);
                beingChecked = button;
                Minecraft.getMinecraft().displayGuiScreen(gui);
            }

            //NerdiCorgi Twitter
            if (button.id == 5005)
            {
                beingCheckedLink = "https://twitter.com/nerdicorgi";
                GuiConfirmOpenLink gui = new GuiConfirmOpenLink(this, beingCheckedLink, 5005, false);
                beingChecked = button;
                Minecraft.getMinecraft().displayGuiScreen(gui);
            }

            //NerdiCorgi Discord
            if (button.id == 5006)
            {
                if (button.x == TransformX(-96 + 1000, PositionType.TOP_RIGHT)) {
                    return;
                }
                beingCheckedLink = "https://discord.gg/3YYYAJa";
                GuiConfirmOpenLink gui = new GuiConfirmOpenLink(this, beingCheckedLink, 5006, false);
                beingChecked = button;
                Minecraft.getMinecraft().displayGuiScreen(gui);
            }

        }
    }

    public GuiButton beingChecked;
    public String beingCheckedLink;
    public void confirmClicked(boolean result, int id)
    {
        if (result)
        {
            String link = null;

            if (beingChecked instanceof GuiButton)
            {
                link = beingCheckedLink;
            }

            if (link != null)
            {
                try
                {
                    Class oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
                    oclass.getMethod("browse", new Class[] { URI.class }).invoke(object, new Object[] { new URI(link) });
                }
                catch (Throwable throwable)
                {
                    LOGGER.error("Couldn\'t open link", throwable);
                }
            }
        }

        this.mc.displayGuiScreen(this);
    }

    public List<GuiButton> getButtonList() {
        return this.buttonList;
    }





    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();

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

        //Background
        int wCenter = this.width / 2;
        int hCenter = this.height / 2;

        if (videoFileExists && showVideo && RealmsOfAvalonModConfig.splashVideoEnabled) {
            this.drawVideoFrame(true, -1);
        }

        if (backgroundExists) {
            if (RealmsOfAvalonModConfig.customMainMenuUsesSplashBackgrounds) {
                this.drawBackground(0, wCenter, hCenter, Textures.splashbg, RealmsOfAvalonModConfig.splashBackgroundBlendEnabled, 0);
            } else {
                this.drawBackground(0, wCenter, hCenter, Textures.menubg, RealmsOfAvalonModConfig.splashBackgroundBlendEnabled, 0);
            }
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


        //logo
        drawCompleteImage(TransformX(-125, PositionType.TOP_CENTER),TransformY(12, PositionType.TOP_CENTER), 250, 35, Textures.menulogo, false, true, 0);


        //Buttons
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawExternalTexture(McUserFace, false, true, 0);

        //Over buttons

        if (RealmsOfAvalonModConfig.splashVideoShowFPS) {
            if (videoFrameDecoder.FPSAVG() >= 25) {
                this.drawCenteredString(this.fontRenderer, "" + videoFrameDecoder.FPSAVG(), this.width - 8, 2, 16777215); //White text
            } else {
                this.drawCenteredString(this.fontRenderer, "" + videoFrameDecoder.FPSAVG(), this.width - 8, 2, 8327184); //Red Text, less than 25 fps for decode
            }
        }

        modUpdateNotification.drawScreen(mouseX, mouseY, partialTicks);


        //Mojang Copyright

        this.drawStringScaled(this.fontRenderer, "Copyright Mojang AB. Do not distribute!", TransformX( this.widthCopyrightRest, PositionType.BOTTOM_LEFT), TransformY(-5, PositionType.BOTTOM_RIGHT), 16777215, 0.5f);

        this.drawStringScaled(this.fontRenderer, "Forge: " + ForgeVersion.getVersion(), TransformX(2, PositionType.BOTTOM_LEFT), TransformY(-15, PositionType.BOTTOM_LEFT), 16777215, 0.5f);
        this.drawStringScaled(this.fontRenderer, "Pack Version: " + RealmsOfAvalonModConfig.PackVersionString, TransformX(2, PositionType.BOTTOM_LEFT), TransformY(-10, PositionType.BOTTOM_LEFT), 16777215, 0.5f);

        int tModCount = Loader.instance().getModList().size();
        int aModCount = Loader.instance().getActiveModList().size();

        this.drawStringScaled(this.fontRenderer, "Mods Loaded: " + tModCount + " (" + aModCount + ")", TransformX(2, PositionType.BOTTOM_LEFT), TransformY(-5, PositionType.BOTTOM_LEFT), 16777215, 0.5f);

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

    public void SetInstances(boolean showFog, List<MovingTexture> clouds, List<MovingTexture> fogs) {
        this.showFog = showFog;
        this.clouds.clear();
        this.fogs.clear();
        this.clouds.addAll(clouds);
        this.fogs.addAll(fogs);
    }
}