package net.petercashel.RealmsOfAvalonMod.TileEntity;

import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.impl.EnergyStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.petercashel.RealmsOfAvalonMod.Blocks.*;

public class TileEntityCartEnergyLoader extends TileEntity implements ITickable, IEnergyReceiver {


    public TileEntityCartEnergyLoader() {
        super();
    }


    public void update() {
        if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 20L == 0L)
        {
            this.blockType = this.getBlockType();

            if (this.blockType instanceof BlockCartEnergyLoader)
            {
                BlockCartEnergyLoader block = ((BlockCartEnergyLoader)this.blockType);
                IBlockState state = this.world.getBlockState(this.pos);

                ((BlockCartEnergyLoader)this.blockType).updateTick(this.world, this.pos, state, block.rand);
            }
        }
    }

    protected EnergyStorage storage = new EnergyStorage(32000);

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

        super.readFromNBT(nbt);
        storage.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

        super.writeToNBT(nbt);
        storage.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return storage.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        if (from == EnumFacing.SOUTH) {
            return true;
        }
        return false;
    }
}
