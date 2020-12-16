package net.petercashel.RealmsOfAvalonMod.TileEntity;

import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyStorage;
import cofh.redstoneflux.impl.EnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;

public abstract class TileEntityBase_PowerGenerator  extends TileEntityBase_Directional implements ITickable, IEnergyProvider, IEnergyStorage, IEnergyConnection {

    public TileEntityBase_PowerGenerator(int PowerCapacity, int TransferRate) {
        super();
        storage = new EnergyStorage(PowerCapacity);
        MaxCapacity = PowerCapacity;
        amountRecv = TransferRate;
        amountSend = TransferRate;
    }

    protected EnergyStorage storage = new EnergyStorage(2000000);
    protected int MaxCapacity = 0;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

        super.readFromNBT(nbt);
        storage = new EnergyStorage(nbt.getInteger("PowerStorageSize"));
        storage.readFromNBT(nbt);
        MaxCapacity = nbt.getInteger("MaxCapacity");

        outputTracker = nbt.getByte("Tracker");
        amountRecv = nbt.getInteger("Recv");
        amountSend = nbt.getInteger("Send");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

        super.writeToNBT(nbt);
        nbt.setInteger("PowerStorageSize", storage.getMaxEnergyStored());
        storage.writeToNBT(nbt);

        nbt.setInteger("MaxCapacity", MaxCapacity);

        nbt.setInteger("Tracker", outputTracker);
        nbt.setInteger("Recv", amountRecv);
        nbt.setInteger("Send", amountSend);
        return nbt;
    }

    @Override
    public void update() {
        super.update();

        //Update energy transfer
        transferEnergy();
        if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 40L == 0L) {
            updateTrackers();
        }
    }

    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
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

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive > amountRecv) {
            maxReceive = amountRecv;
        }
        this.markDirty();
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract > amountSend) {
            maxExtract = amountSend;
        }
        this.markDirty();
        return storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return storage.getMaxEnergyStored();
    }

    /**
     * Copied from Thermal Expansion's TileCell
     */

    private int compareTracker;
    private int meterTracker;
    protected int outputTracker;

    public int amountRecv = 25000;
    public int amountSend = 25000;


    public int getScaledEnergyStored(int scale) {

        return MathHelper.round((long) storage.getEnergyStored() * scale / MaxCapacity);
    }

    protected void transferEnergy() {
        for (int i = outputTracker; i < 6 && storage.getEnergyStored() > 0; i++) {
            //if (Facing != null && i == Facing.getOpposite().getIndex()) {
                storage.modifyEnergyStored(-EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[i], Math.min(amountSend, storage.getEnergyStored()), false));
            //}
        }
        for (int i = 0; i < outputTracker && storage.getEnergyStored() > 0; i++) {
            //if (Facing != null && i == Facing.getOpposite().getIndex()) {
                storage.modifyEnergyStored(-EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[i], Math.min(amountSend, storage.getEnergyStored()), false));
            //}
        }
        outputTracker++;
        outputTracker %= 6;
    }

    protected void updateTrackers() {
        int curScale = storage.getEnergyStored() > 0 ? 1 + getScaledEnergyStored(14) : 0;
        if (curScale != compareTracker) {
            compareTracker = curScale;
            //callNeighborTileChange();
        }
        curScale = storage.getEnergyStored() > 0 ? 1 + Math.min(getScaledEnergyStored(8), 7) : 0;
        if (meterTracker != curScale) {
            meterTracker = curScale;
            //sendTilePacket(Side.CLIENT);
        }
    }

    /* CAPABILITIES */
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing from) {

        return capability != CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, from));
    }

    @Override
    public <T> T getCapability(Capability<T> capability, final EnumFacing from) {
        if (capability == CapabilityEnergy.ENERGY && Facing != null && Facing == from) {
            return CapabilityEnergy.ENERGY.cast(new net.minecraftforge.energy.IEnergyStorage() {

                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    return TileEntityBase_PowerGenerator.this.receiveEnergy(from, maxReceive, simulate);
                }

                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {

                    return TileEntityBase_PowerGenerator.this.extractEnergy(from, maxExtract, simulate);
                }

                @Override
                public int getEnergyStored() {

                    return TileEntityBase_PowerGenerator.this.getEnergyStored(from);
                }

                @Override
                public int getMaxEnergyStored() {

                    return TileEntityBase_PowerGenerator.this.getMaxEnergyStored(from);
                }

                @Override
                public boolean canExtract() {

                    return true;
                }

                @Override
                public boolean canReceive() {
                    return false;
                }
            });
        }
        return super.getCapability(capability, from);
    }
}
