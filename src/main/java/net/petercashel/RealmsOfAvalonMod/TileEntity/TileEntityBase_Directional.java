package net.petercashel.RealmsOfAvalonMod.TileEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockBase_Directional;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartEnergyUnLoader;

public class TileEntityBase_Directional extends TileEntity implements ITickable {
    protected EnumFacing Facing = null;

    public void SetFacing(EnumFacing direction) {
        this.Facing = direction;
    }

    public TileEntityBase_Directional() {
        super();
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

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        Facing = EnumFacing.getFront(nbt.getInteger("FacingInt"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (Facing != null) {
            nbt.setInteger("FacingInt", Facing.getIndex());
        } else {
            nbt.setInteger("FacingInt", 0);
        }
        return nbt;
    }


    @Override
    public void update() {
        if (Facing == null) {
            //No facing? go get it.
            if (this.world != null && !this.world.isRemote)
            {
                this.blockType = this.getBlockType();
                if (this.blockType instanceof BlockBase_Directional)
                {
                    IBlockState state = this.world.getBlockState(this.pos);
                    if (this.Facing == null) {
                        this.Facing = ((BlockBase_Directional)blockType).GetFacing(state);
                    }
                }
            }
        }
    }
}
