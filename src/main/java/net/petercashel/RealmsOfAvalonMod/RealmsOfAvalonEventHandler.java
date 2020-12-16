package net.petercashel.RealmsOfAvalonMod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.minecart.MinecartEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.petercashel.RealmsOfAvalonMod.TileEntity.TileEntityCartEnergyUnLoader;

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
}
