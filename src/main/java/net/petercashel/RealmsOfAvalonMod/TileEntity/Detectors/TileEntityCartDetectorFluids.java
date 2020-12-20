package net.petercashel.RealmsOfAvalonMod.TileEntity.Detectors;

import net.minecraft.block.state.IBlockState;
import net.petercashel.RealmsOfAvalonMod.Blocks.Core.BlockCartLoaderBase;
import net.petercashel.RealmsOfAvalonMod.TileEntity.Core.TileEntityBase_Container;

public class TileEntityCartDetectorFluids extends TileEntityBase_Container {
    public TileEntityCartDetectorFluids() {
        super(9, false, false);
    }


    public void update() {
        super.update();
        if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 10L == 0L)
        {
            this.blockType = this.getBlockType();

            if (this.blockType instanceof BlockCartLoaderBase)
            {
                BlockCartLoaderBase block = ((BlockCartLoaderBase)this.blockType);
                IBlockState state = this.world.getBlockState(this.pos);

                ((BlockCartLoaderBase)this.blockType).updateTick(this.world, this.pos, state, block.rand);
            }
        }
    }

    @Override
    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container.realmsofavalonmod.cartdetectorfluids";
    }
}
