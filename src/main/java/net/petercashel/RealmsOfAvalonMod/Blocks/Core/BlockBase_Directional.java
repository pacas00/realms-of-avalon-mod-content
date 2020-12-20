package net.petercashel.RealmsOfAvalonMod.Blocks.Core;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockBase_Directional extends BlockContainer {

    public static final PropertyDirection FACING = BlockDirectional.FACING;

    protected BlockBase_Directional(Material materialIn) {
        super(materialIn);
    }

    public EnumFacing GetFacing(IBlockState state) {
        return ((EnumFacing)state.getValue(FACING));
    }
}
