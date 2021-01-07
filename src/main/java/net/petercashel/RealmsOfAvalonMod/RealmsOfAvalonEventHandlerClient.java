package net.petercashel.RealmsOfAvalonMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.petercashel.RealmsOfAvalonMod.GUI.Servers.GuiMultiplayerPack;
import net.petercashel.RealmsOfAvalonMod.GUI.Splash.GuiSplashScreenPack;

import java.io.File;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RealmsOfAvalonEventHandlerClient {
    @SideOnly(Side.CLIENT)
    private static boolean launchedSplashScreen = false;
    @SideOnly(Side.CLIENT)
    private static GuiButton serverListButton = null;
    @SideOnly(Side.CLIENT)
    private static GuiButton splashButton = null;

    @SideOnly(Side.CLIENT)
    private static boolean hasROAServerConfig() {
        return new File(new File(Minecraft.getMinecraft().mcDataDir, "config"), RealmsOfAvalonModConfig.serverListFileName).exists();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onEvent(GuiScreenEvent.InitGuiEvent event) {
        if (RealmsOfAvalonModConfig.debugLogging) RealmsOfAvalonMod.instance.logger.warn(event);
        if (event instanceof GuiScreenEvent.InitGuiEvent.Post) {
            if (event.getGui() instanceof GuiMainMenu) {
                GuiMainMenu mm = (GuiMainMenu) event.getGui();
                String name = event.getGui().getClass().getSimpleName();
                if (name.equals("GuiFakeMain") && RealmsOfAvalonModConfig.serverListEnabled) { //By ensureing CMM is loaded, the button is automatically hidden.
                    java.util.List<GuiButton> buttons = event.getButtonList();
                    if (serverListButton == null) {
                        serverListButton = new GuiButton(7001, mm.width / 2 - 154, mm.height - 28, 100, 20, I18n.format("gui.realmsofavalonmod.serverbutton"));
                        splashButton = new GuiButton(7002, mm.width / 2 - 154, -1000, 100, 20, I18n.format("gui.realmsofavalonmod.serverbutton"));
                        buttons.add(serverListButton);
                        buttons.add(splashButton);
                        if (RealmsOfAvalonModConfig.debugLogging) RealmsOfAvalonMod.instance.logger.warn("Added Button to menu");
                    }
                    if (!buttons.contains(serverListButton)) {
                        buttons.add(serverListButton);
                        buttons.add(splashButton);
                        if (RealmsOfAvalonModConfig.debugLogging) RealmsOfAvalonMod.instance.logger.warn("Added Button to menu");
                    }
                    event.setButtonList(buttons);
                } else {
                    if (RealmsOfAvalonModConfig.debugLogging) RealmsOfAvalonMod.instance.logger.warn("Had Mainmenu, but with name " + name);
                }
                if (launchedSplashScreen == false && name.equals("GuiFakeMain") && RealmsOfAvalonModConfig.splashEnabled) {
                    launchedSplashScreen = true;

                    GuiSplashScreenPack pack = new GuiSplashScreenPack(event.getGui());
                    Minecraft.getMinecraft().displayGuiScreen((GuiScreen)pack);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onAPEvent(GuiScreenEvent.ActionPerformedEvent.Post event) {
        GuiButton b = event.getButton();
        if (b.id == 7001 && RealmsOfAvalonModConfig.serverListEnabled) //Hopefully it's our custom main menu button.
        {
            GuiMultiplayerPack pack = new GuiMultiplayerPack(event.getGui());
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)pack);
        }
        if (b.id == 7002 && RealmsOfAvalonModConfig.splashEnabled) //Hopefully it's our custom main menu button.
        {
            GuiSplashScreenPack pack = new GuiSplashScreenPack(event.getGui());
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)pack);
        }
    }


}
