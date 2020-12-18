package net.petercashel.RealmsOfAvalonMod.TileEntity;

import net.minecraft.block.state.IBlockState;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartItemUnLoader;

public class TileEntityCartItemUnLoader extends TileEntityBase_Container {
    public TileEntityCartItemUnLoader() {
        super(9, true, false);
    }

    @Override
    public void update() {
        super.update();
        if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 2L == 0L)
        {
            this.blockType = this.getBlockType();
            if (this.blockType instanceof BlockCartItemUnLoader)
            {
                BlockCartItemUnLoader block = ((BlockCartItemUnLoader)this.blockType);
                IBlockState state = this.world.getBlockState(this.pos);
                ((BlockCartItemUnLoader)this.blockType).updateTick(this.world, this.pos, state, block.rand);
            }
        }
    }
}
