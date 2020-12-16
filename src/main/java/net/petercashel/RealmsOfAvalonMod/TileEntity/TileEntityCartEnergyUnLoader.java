package net.petercashel.RealmsOfAvalonMod.TileEntity;

import cofh.api.tileentity.IEnergyInfo;
import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.api.IEnergyStorage;
import cofh.redstoneflux.impl.EnergyStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartEnergyLoader;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartEnergyUnLoader;

public class TileEntityCartEnergyUnLoader extends TileEntity implements ITickable, IEnergyProvider, IEnergyStorage, IEnergyConnection {

    private EnumFacing Facing = null;

    public TileEntityCartEnergyUnLoader() {
        super();
    }


    public void update() {
        //Update cart look
        if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 20L == 0L)
        {
            this.blockType = this.getBlockType();

            if (this.blockType instanceof BlockCartEnergyUnLoader)
            {
                BlockCartEnergyUnLoader block = ((BlockCartEnergyUnLoader)this.blockType);
                IBlockState state = this.world.getBlockState(this.pos);

                ((BlockCartEnergyUnLoader)this.blockType).updateTick(this.world, this.pos, state, block.rand);

                if (this.Facing == null) {
                    this.Facing = block.GetFacing(state);
                }
            }
        }

        //Update energy transfer
        transferEnergy();
        if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 40L == 0L) {
            updateTrackers();
        }
    }

    /**
     * Called from Chunk.setBlockIDWithMetadata and Chunk.fillChunk, determines if this tile entity should be re-created when the ID, or Metadata changes.
     * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
     *
     * @param world Current world
     * @param pos Tile's world position
     * @param oldState The old ID of the block
     * @param newSate The new ID of the block (May be the same)
     * @return true forcing the invalidation of the existing TE, false not to invalidate the existing TE
     */
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }


    protected EnergyStorage storage = new EnergyStorage(32000);

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        storage = new EnergyStorage(getMaxCapacity(level, enchantHolding));
        storage.readFromNBT(nbt);

        enchantHolding = nbt.getShort("EncHolding");
        outputTracker = nbt.getByte("Tracker");
        amountRecv = nbt.getInteger("Recv");
        amountSend = nbt.getInteger("Send");

        Facing = EnumFacing.getFront(nbt.getInteger("FacingInt"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

        super.writeToNBT(nbt);
        storage.writeToNBT(nbt);

        nbt.setShort("EncHolding", enchantHolding);
        nbt.setInteger("Tracker", outputTracker);
        nbt.setInteger("Recv", amountRecv);
        nbt.setInteger("Send", amountSend);

        if (Facing != null) {
            nbt.setInteger("FacingInt", Facing.getIndex());
        } else {
            nbt.setInteger("FacingInt", 0);
        }

        return nbt;
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
    public boolean canConnectEnergy(EnumFacing from) {
        if (Facing != null && from == Facing.getOpposite()) {
            return true;
        }
        return false;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
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

    public static final int CAPACITY_BASE = 32000;
    public static final int[] CAPACITY = { 1, 4, 9, 16, 25 };

    private int compareTracker;
    private int meterTracker;
    private int outputTracker;

    public short enchantHolding;
    public int amountRecv = 8000;
    public int amountSend = 8000;

    public int level = 0;

    /* COMMON METHODS */
    public static int getMaxCapacity(int level, int enchant) {

        return (int) (CAPACITY_BASE * (Math.max(CAPACITY[MathHelper.clamp(level, 0, 4)] + (CAPACITY[MathHelper.clamp(level, 0, 4)] * (double) enchant) / 2, 0)));
    }

    public int getScaledEnergyStored(int scale) {

        return MathHelper.round((long) storage.getEnergyStored() * scale / getMaxCapacity(level, enchantHolding));
    }

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
                    return TileEntityCartEnergyUnLoader.this.receiveEnergy(from, maxReceive, simulate);
                }

                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {

                    return TileEntityCartEnergyUnLoader.this.extractEnergy(from, maxExtract, simulate);
                }

                @Override
                public int getEnergyStored() {

                    return TileEntityCartEnergyUnLoader.this.getEnergyStored(from);
                }

                @Override
                public int getMaxEnergyStored() {

                    return TileEntityCartEnergyUnLoader.this.getMaxEnergyStored(from);
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
