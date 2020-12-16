package net.petercashel.RealmsOfAvalonMod.TileEntity;

import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartDetector;

public class TileEntityCartDetector extends TileEntity implements ITickable {


    public TileEntityCartDetector() {
        super();
    }


    public void update() {
        if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 10L == 0L)
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
}
