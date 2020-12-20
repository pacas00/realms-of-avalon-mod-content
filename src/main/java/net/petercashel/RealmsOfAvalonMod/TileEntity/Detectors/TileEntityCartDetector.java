package net.petercashel.RealmsOfAvalonMod.TileEntity.Detectors;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ITickable;
import net.petercashel.RealmsOfAvalonMod.Blocks.Core.BlockCartLoaderBase;
import net.petercashel.RealmsOfAvalonMod.TileEntity.Core.TileEntityBase_Directional;

public class TileEntityCartDetector extends TileEntityBase_Directional implements ITickable {


    public TileEntityCartDetector() {
        super();
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

}
