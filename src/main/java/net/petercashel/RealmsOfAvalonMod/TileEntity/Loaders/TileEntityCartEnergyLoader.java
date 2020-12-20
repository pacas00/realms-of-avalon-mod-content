package net.petercashel.RealmsOfAvalonMod.TileEntity.Loaders;

import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.petercashel.RealmsOfAvalonMod.Blocks.Loaders.BlockCartEnergyLoader;
import net.petercashel.RealmsOfAvalonMod.TileEntity.Core.TileEntityBase_PowerConsumer;

public class TileEntityCartEnergyLoader extends TileEntityBase_PowerConsumer implements ITickable, IEnergyReceiver {

    public TileEntityCartEnergyLoader() {
        super(2000000, 25000);
    }

    @Override
    public void update() {
        super.update();
        if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 2L == 0L)
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

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        if (Facing != null && from == Facing.getOpposite()) {
            return true;
        }
        return false;
    }
}
