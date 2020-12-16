package net.petercashel.RealmsOfAvalonMod.TileEntity;

import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.impl.EnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public abstract class TileEntityBase_PowerConsumer extends TileEntityBase_Directional implements ITickable, IEnergyReceiver {

    public TileEntityBase_PowerConsumer(int PowerCapacity, int TransferRate) {
        super();
        storage = new EnergyStorage(PowerCapacity);
        amountRecv = TransferRate;
        amountSend = TransferRate;
    }

    protected EnergyStorage storage = new EnergyStorage(2000000);
    public int amountRecv = 25000;
    public int amountSend = 25000;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

        super.readFromNBT(nbt);
        storage = new EnergyStorage(nbt.getInteger("PowerStorageSize"));
        storage.readFromNBT(nbt);
        amountRecv = nbt.getInteger("Recv");
        amountSend = nbt.getInteger("Send");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

        super.writeToNBT(nbt);
        nbt.setInteger("PowerStorageSize", storage.getMaxEnergyStored());
        storage.writeToNBT(nbt);
        nbt.setInteger("Recv", amountRecv);
        nbt.setInteger("Send", amountSend);
        return nbt;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        if (maxReceive > amountRecv) {
            maxReceive = amountRecv;
        }
        this.markDirty();
        return storage.receiveEnergy(maxReceive, simulate);
    }

    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        if (maxExtract > amountSend) {
            maxExtract = amountSend;
        }
        this.markDirty();
        return storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return storage.getMaxEnergyStored();
    }

}
