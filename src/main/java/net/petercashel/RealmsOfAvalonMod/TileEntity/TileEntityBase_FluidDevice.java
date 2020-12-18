package net.petercashel.RealmsOfAvalonMod.TileEntity;

import cofh.core.fluid.FluidTankCore;
import cofh.core.util.helpers.FluidHelper;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.impl.EnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileEntityBase_FluidDevice extends TileEntityBase_Directional implements ITickable, IFluidHandler {

    public TileEntityBase_FluidDevice(int FluidCapactity, int TransferRate, boolean AllowExtraction, boolean AllowInsert, boolean AutoOutput) {
        super();
        this.FluidCapactity = FluidCapactity;
        this.TransferRate = TransferRate;
        this.AllowExtraction = AllowExtraction;
        this.AllowInsert = AllowInsert;
        this.AutoOutput = AutoOutput;
        tank = new FluidTank(1000 * FluidCapactity);

    }
    private FluidTank tank = null;

    public int FluidCapactity = 0;
    public int TransferRate = 0;
    public boolean AllowExtraction = false;
    public boolean AllowInsert = false;
    public boolean AutoOutput = false;

    private int targetWater = -1;
    private int outputTracker;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        FluidCapactity = nbt.getInteger("FluidCapactity");
        TransferRate = nbt.getInteger("TransferRate");
        outputTracker = nbt.getInteger("outputTracker");

        AllowExtraction = nbt.getBoolean("AllowExtraction");
        AllowInsert = nbt.getBoolean("AllowInsert");
        AutoOutput = nbt.getBoolean("AutoOutput");

        tank.readFromNBT(nbt);

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

        super.writeToNBT(nbt);

        nbt.setInteger("FluidCapactity", FluidCapactity);
        nbt.setInteger("TransferRate", TransferRate);
        nbt.setInteger("outputTracker", outputTracker);

        nbt.setBoolean("AllowExtraction", AllowExtraction);
        nbt.setBoolean("AllowInsert", AllowInsert);
        nbt.setBoolean("AutoOutput", AutoOutput);

        return nbt;
    }

    @Override
    public void update() {
        super.update();
        transferOutput();
    }

    protected void transferOutput() {
        if (!AutoOutput || tank.getFluidAmount() <= 0) {
            return;
        }
        int side;
        FluidStack output = new FluidStack(tank.getFluid(), tank.getFluidAmount());
        for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
            side = i % 6;
            if (Facing.getIndex() == side && (AllowExtraction || AutoOutput)) {
                int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[side].getOpposite(), output, true);
                if (toDrain > 0) {
                    tank.drain(toDrain, true);
                    outputTracker = side;
                    break;
                }
            }
        }
    }


    public FluidTank getTank() {
        return tank;
    }

    public FluidStack getTankFluid() {
        return tank.getFluid();
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && Facing != null && Facing == facing.getOpposite()) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && Facing != null && Facing == from.getOpposite()) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {

                @Override
                public IFluidTankProperties[] getTankProperties() {

                    FluidTankInfo info = tank.getInfo();
                    return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, AllowInsert, AllowExtraction) };
                }

                @Override
                public int fill(FluidStack resource, boolean doFill) {
                    if (from == null || allowInsert(from)) {
                        return tank.fill(resource, doFill);
                    }
                    return 0;
                }

                @Nullable
                @Override
                public FluidStack drain(FluidStack resource, boolean doDrain) {

                    if (from == null || allowExtract(from)) {
                        return tank.drain(resource, doDrain);
                    }
                    return null;
                }

                @Nullable
                @Override
                public FluidStack drain(int maxDrain, boolean doDrain) {

                    if (from == null || allowExtract(from)) {
                        return tank.drain(maxDrain, doDrain);
                    }
                    return null;
                }
            });
        }
        return super.getCapability(capability, from);
    }

    private boolean allowInsert(EnumFacing from) {
        if (!AllowInsert) return false;
        if (from == this.Facing.getOpposite()) {
            return true;
        }
        return false;
    }

    private boolean allowExtract(EnumFacing from) {
        if (!AllowExtraction) return false;
        if (from == this.Facing.getOpposite()) {
            return true;
        }
        return false;
    }

    public int getFluidStored(EnumFacing facing) {
        return this.tank.getFluidAmount();
    }

    public int getMaxFluidStored(EnumFacing facing) {
        return this.tank.getCapacity();
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return tank.getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return tank.drain(resource, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }
}
