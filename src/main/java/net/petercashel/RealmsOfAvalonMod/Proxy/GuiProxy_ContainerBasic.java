package net.petercashel.RealmsOfAvalonMod.Proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.petercashel.RealmsOfAvalonMod.Container.ContainerBasic;
import net.petercashel.RealmsOfAvalonMod.GUI.ContainerBasicGui;
import net.petercashel.RealmsOfAvalonMod.TileEntity.Core.TileEntityBase_Container;

public class GuiProxy_ContainerBasic implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBase_Container) {
            return new ContainerBasic(player.inventory, (TileEntityBase_Container) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBase_Container) {
            TileEntityBase_Container containerTileEntity = (TileEntityBase_Container) te;
            return new ContainerBasicGui(containerTileEntity, new ContainerBasic(player.inventory, containerTileEntity));
        }
        return null;
    }
}
