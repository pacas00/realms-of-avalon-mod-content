package net.petercashel.RealmsOfAvalonMod.TileEntity.Loaders;

import net.minecraft.block.state.IBlockState;
import net.petercashel.RealmsOfAvalonMod.Blocks.Loaders.BlockCartItemUnLoader;
import net.petercashel.RealmsOfAvalonMod.TileEntity.Core.TileEntityBase_Container;

public class TileEntityCartItemUnLoader extends TileEntityBase_Container {
    public TileEntityCartItemUnLoader() {
        super(27, true, false);
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

    @Override
    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container.realmsofavalonmod.cartitemunloader";
    }
}
