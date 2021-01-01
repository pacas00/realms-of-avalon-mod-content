package net.petercashel.RealmsOfAvalonMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.petercashel.RealmsOfAvalonMod.GUI.Servers.GuiMultiplayerPack;

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



    @SubscribeEvent(priority = EventPriority.LOW)
    @SideOnly(Side.CLIENT)
    public static void openGui(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiMainMenu) {
            GuiMainMenu mm = (GuiMainMenu) event.getGui();

        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public static void onScreenEvent(GuiScreenEvent.InitGuiEvent event) {
        if (event.getGui() instanceof GuiMainMenu) {
            GuiMainMenu mm = (GuiMainMenu) event.getGui();

        }

    }

    @SubscribeEvent(priority = EventPriority.LOW)
    @SideOnly(Side.CLIENT)
    public static void onAPEvent(GuiScreenEvent.ActionPerformedEvent.Post event) {
        GuiButton b = event.getButton();
        if (b.displayString.equals("Official Servers") && b.id >= 6000) //Hopefully, Custom main menu button.
        {
            GuiMultiplayerPack pack = new GuiMultiplayerPack(event.getGui());
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)pack);
        }

    }
}
