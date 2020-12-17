package net.petercashel.RealmsOfAvalonMod.TileEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartFluidLoader;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartFluidUnLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityCartFluidUnLoader extends TileEntityBase_FluidDevice implements ITickable {

    public TileEntityCartFluidUnLoader() {
        super(32, 8000, true, false, true);
    }

    @Override
    public void update() {
        super.update();
        if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 2L == 0L)
        {
            this.blockType = this.getBlockType();
            if (this.blockType instanceof BlockCartFluidUnLoader)
            {
                BlockCartFluidUnLoader block = ((BlockCartFluidUnLoader)this.blockType);
                IBlockState state = this.world.getBlockState(this.pos);
                ((BlockCartFluidUnLoader)this.blockType).updateTick(this.world, this.pos, state, block.rand);
            }
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return super.getCapability(capability, facing);
    }
}
