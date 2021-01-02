package net.petercashel.RealmsOfAvalonMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.petercashel.RealmsOfAvalonMod.GUI.Servers.GuiMultiplayerPack;
import net.petercashel.RealmsOfAvalonMod.GUI.Splash.GuiSplashScreenPack;

import java.io.File;

@Mod.EventBusSubscriber()
public class RealmsOfAvalonEventHandler {

    //Stop Energy Cart from being opened.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void OnPlayerPlayerInteractEvent(PlayerInteractEvent event) {
        if (event instanceof PlayerInteractEvent.EntityInteractSpecific) {
            Entity target = ((PlayerInteractEvent.EntityInteractSpecific) event).getTarget();
            if (target == null || !(target instanceof EntityMinecartChest)) {
                return;
            }
            EntityMinecartChest chestCart = (EntityMinecartChest) target;

            if (chestCart.getTags().stream().filter(t -> t.startsWith("Energy")).count() != 0) {
                String energyTag = chestCart.getTags().stream().filter(t -> t.startsWith("Energy")).findFirst().get();
                String maxEnergyTag = chestCart.getTags().stream().filter(t -> t.startsWith("MaxEnergy")).findFirst().get();

                int currEnergy = 0;
                int maxEnergy = 0;
                currEnergy = Integer.parseInt(energyTag.split(":")[1]);
                maxEnergy = Integer.parseInt(maxEnergyTag.split(":")[1]);

                ((PlayerInteractEvent.EntityInteractSpecific) event).getEntityPlayer().sendStatusMessage(new TextComponentString("Stored: " + currEnergy + "/" + maxEnergy), true);

                if (event.isCancelable()) {
                    event.getEntityPlayer().closeScreen();
                    event.setResult(Event.Result.DENY);
                    event.setCancellationResult(EnumActionResult.FAIL);
                    event.setCanceled(true);
                }
            }
        }
    }



    @SideOnly(Side.CLIENT)
    private static boolean launchedSplashScreen = false;
    @SideOnly(Side.CLIENT)
    private static GuiButton serverListButton = null;

    @SideOnly(Side.CLIENT)
    private static boolean hasROAServerConfig() {
        return new File(new File(Minecraft.getMinecraft().mcDataDir, "config"), "RealmsOfAvalonServers.nbt").exists();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onEvent(GuiScreenEvent.InitGuiEvent event) {
        RealmsOfAvalonMod.instance.logger.warn(event);
        if (event instanceof GuiScreenEvent.InitGuiEvent.Post) {
            if (event.getGui() instanceof GuiMainMenu) {
                GuiMainMenu mm = (GuiMainMenu) event.getGui();
                String name = event.getGui().getClass().getSimpleName();
                if (name.equals("GuiFakeMain") && hasROAServerConfig()) { //By ensureing CMM is loaded, the button is automatically hidden.
                    java.util.List<net.minecraft.client.gui.GuiButton> buttons = event.getButtonList();
                    if (serverListButton == null) {
                        serverListButton = new GuiButton(7001, mm.width / 2 - 154, mm.height - 28, 100, 20, I18n.format("gui.realmsofavalonmod.serverbutton"));
                        buttons.add(serverListButton);
                        RealmsOfAvalonMod.instance.logger.warn("Added Button to menu");
                    }
                    if (!buttons.contains(serverListButton)) {
                        buttons.add(serverListButton);
                        RealmsOfAvalonMod.instance.logger.warn("Added Button to menu");
                    }
                    event.setButtonList(buttons);
                } else {
                    RealmsOfAvalonMod.instance.logger.warn("Had Mainmenu, but with name " + name);
                }
                if (launchedSplashScreen == false && name.equals("GuiFakeMain") && hasROAServerConfig()) {
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
        if (b.id == 7001 && hasROAServerConfig()) //Hopefully it's our custom main menu button.
        {
            GuiMultiplayerPack pack = new GuiMultiplayerPack(event.getGui());
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)pack);
        }

    }


}
