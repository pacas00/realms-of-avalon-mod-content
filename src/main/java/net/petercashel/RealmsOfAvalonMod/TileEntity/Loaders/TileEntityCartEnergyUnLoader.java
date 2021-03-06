package net.petercashel.RealmsOfAvalonMod.TileEntity.Loaders;

import cofh.core.util.helpers.EnergyHelper;
import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.petercashel.RealmsOfAvalonMod.Blocks.Loaders.BlockCartEnergyUnLoader;
import net.petercashel.RealmsOfAvalonMod.TileEntity.Core.TileEntityBase_PowerGenerator;

public class TileEntityCartEnergyUnLoader extends TileEntityBase_PowerGenerator implements ITickable, IEnergyProvider, IEnergyStorage, IEnergyConnection {
    public TileEntityCartEnergyUnLoader() {
        super(2000000, 25000);
    }


    public void update() {
        super.update();
        //Update cart look
        if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 2L == 0L)
        {
            this.blockType = this.getBlockType();
            if (this.blockType instanceof BlockCartEnergyUnLoader)
            {
                BlockCartEnergyUnLoader block = ((BlockCartEnergyUnLoader)this.blockType);
                IBlockState state = this.world.getBlockState(this.pos);
                ((BlockCartEnergyUnLoader)this.blockType).updateTick(this.world, this.pos, state, block.rand);
            }
        }
    }


    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        if (Facing != null && from == Facing.getOpposite()) {
            return true;
        }
        return false;
    }

    @Override
    protected void transferEnergy() {
        for (int i = outputTracker; i < 6 && storage.getEnergyStored() > 0; i++) {
            if (Facing != null && i == Facing.getOpposite().getIndex()) {
            storage.modifyEnergyStored(-EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[i], Math.min(amountSend, storage.getEnergyStored()), false));
            }
        }
        for (int i = 0; i < outputTracker && storage.getEnergyStored() > 0; i++) {
            if (Facing != null && i == Facing.getOpposite().getIndex()) {
            storage.modifyEnergyStored(-EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[i], Math.min(amountSend, storage.getEnergyStored()), false));
            }
        }
        outputTracker++;
        outputTracker %= 6;
    }



}
