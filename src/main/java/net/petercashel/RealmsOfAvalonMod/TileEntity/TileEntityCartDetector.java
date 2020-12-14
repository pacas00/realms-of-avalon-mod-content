package net.petercashel.RealmsOfAvalonMod.TileEntity;

import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartDetector;

public class TileEntityCartDetector extends TileEntity implements ITickable {


    public TileEntityCartDetector() {
        super();
    }


    public void update() {
        if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 20L == 0L)
        {
            this.blockType = this.getBlockType();

            if (this.blockType instanceof BlockCartDetector)
            {
                BlockCartDetector block = ((BlockCartDetector)this.blockType);
                IBlockState state = this.world.getBlockState(this.pos);

                ((BlockCartDetector)this.blockType).updateTick(this.world, this.pos, state, block.rand);
            }
        }
    }
}
