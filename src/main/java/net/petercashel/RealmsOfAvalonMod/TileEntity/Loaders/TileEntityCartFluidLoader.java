package net.petercashel.RealmsOfAvalonMod.TileEntity.Loaders;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ITickable;
import net.petercashel.RealmsOfAvalonMod.Blocks.Loaders.BlockCartFluidLoader;
import net.petercashel.RealmsOfAvalonMod.TileEntity.Core.TileEntityBase_FluidDevice;

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
