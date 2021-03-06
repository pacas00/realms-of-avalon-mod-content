package net.petercashel.RealmsOfAvalonMod.TileEntity.Loaders;

import net.minecraft.block.state.IBlockState;
import net.petercashel.RealmsOfAvalonMod.Blocks.Loaders.BlockCartItemLoader;
import net.petercashel.RealmsOfAvalonMod.TileEntity.Core.TileEntityBase_Container;

public class TileEntityCartItemLoader extends TileEntityBase_Container {
    public TileEntityCartItemLoader() {
        super(27, false, true);
    }

    @Override
    public void update() {
        super.update();
        if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 20L == 0L)
        {
            this.blockType = this.getBlockType();
            if (this.blockType instanceof BlockCartItemLoader)
            {
                BlockCartItemLoader block = ((BlockCartItemLoader)this.blockType);
                IBlockState state = this.world.getBlockState(this.pos);
                ((BlockCartItemLoader)this.blockType).updateTick(this.world, this.pos, state, block.rand);
            }
        }
    }

    @Override
    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container.realmsofavalonmod.cartitemloader";
    }
}
