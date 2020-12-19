package net.petercashel.RealmsOfAvalonMod.TileEntity;

import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartDetector;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartLoaderBase;

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
