package net.petercashel.RealmsOfAvalonMod.TileEntity;

import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartEnergyLoader;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartFluidLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityCartFluidLoader extends TileEntityBase_FluidDevice implements ITickable {

    public TileEntityCartFluidLoader() {
        super(32, 8000, false, true, false);
    }

    @Override
    public void update() {
        super.update();
        if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 2L == 0L)
        {
            this.blockType = this.getBlockType();
            if (this.blockType instanceof BlockCartFluidLoader)
            {
                BlockCartFluidLoader block = ((BlockCartFluidLoader)this.blockType);
                IBlockState state = this.world.getBlockState(this.pos);
                ((BlockCartFluidLoader)this.blockType).updateTick(this.world, this.pos, state, block.rand);
            }
        }
    }
}
