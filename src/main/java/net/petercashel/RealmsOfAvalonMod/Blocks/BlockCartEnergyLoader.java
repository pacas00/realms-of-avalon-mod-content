package net.petercashel.RealmsOfAvalonMod.Blocks;

import com.google.common.base.Predicate;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.petercashel.RealmsOfAvalonMod.Interfaces.IInitEvents;
import net.petercashel.RealmsOfAvalonMod.RealmsOfAvalonMod;
import net.petercashel.RealmsOfAvalonMod.TileEntity.*;

import java.util.List;
import java.util.Random;

public class BlockCartEnergyLoader extends BlockContainer implements IInitEvents, ITileEntityProvider {

    public static final PropertyDirection FACING = BlockDirectional.FACING;
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public Random rand = new Random();

    public BlockCartEnergyLoader() {
        super(Material.ROCK);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setCreativeTab(CreativeTabs.REDSTONE);
        this.requiresUpdates();
        this.needsRandomTick = true;
    }

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);
        this.setDefaultDirection(worldIn, pos, state);
    }

    private void setDefaultDirection(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
            boolean flag = worldIn.getBlockState(pos.north()).isFullBlock();
            boolean flag1 = worldIn.getBlockState(pos.south()).isFullBlock();

            if (enumfacing == EnumFacing.NORTH && flag && !flag1)
            {
                enumfacing = EnumFacing.SOUTH;
            }
            else if (enumfacing == EnumFacing.SOUTH && flag1 && !flag)
            {
                enumfacing = EnumFacing.NORTH;
            }
            else
            {
                boolean flag2 = worldIn.getBlockState(pos.west()).isFullBlock();
                boolean flag3 = worldIn.getBlockState(pos.east()).isFullBlock();

                if (enumfacing == EnumFacing.WEST && flag2 && !flag3)
                {
                    enumfacing = EnumFacing.EAST;
                }
                else if (enumfacing == EnumFacing.EAST && flag3 && !flag2)
                {
                    enumfacing = EnumFacing.WEST;
                }
            }
        }
    }

    /**
     * Called when the block is right clicked by a player.
     */
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityCartEnergyLoader)
            {
                playerIn.sendStatusMessage(new TextComponentString("Stored: " + ((TileEntityCartEnergyLoader)tileentity).getEnergyStored(EnumFacing.SOUTH) + "/" + ((TileEntityCartEnergyLoader)tileentity).getMaxEnergyStored(EnumFacing.SOUTH)), true);
            }

            return true;
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityCartEnergyLoader();
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)), 2);

        if (stack.hasDisplayName())
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityCartEnergyLoader)
            {
                //((TileEntityCartEnergyLoader)tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityCartEnergyLoader)
        {
//            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityDispenser)tileentity);
//            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    /**
     * Get the position where the dispenser at the given Coordinates should dispense to.
     */
    public static IPosition getDispensePosition(IBlockSource coords)
    {
        EnumFacing enumfacing = (EnumFacing)coords.getBlockState().getValue(FACING);
        double d0 = coords.getX() + 0.7D * (double)enumfacing.getFrontOffsetX();
        double d1 = coords.getY() + 0.7D * (double)enumfacing.getFrontOffsetY();
        double d2 = coords.getZ() + 0.7D * (double)enumfacing.getFrontOffsetZ();
        return new PositionImpl(d0, d1, d2);
    }

    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7)).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getIndex();

        if (((Boolean)state.getValue(POWERED)).booleanValue())
        {
            i |= 8;
        }

        return i;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING))).withProperty(POWERED, false);
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, POWERED});
    }

     /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            this.updatePoweredState(worldIn, pos, state);
        }
    }

    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return ((Boolean)blockState.getValue(POWERED)).booleanValue() ? 15 : 0;
    }

    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        if (!((Boolean)blockState.getValue(POWERED)).booleanValue())
        {
            return 0;
        }
        else
        {
            return side == EnumFacing.UP ? 15 : 0;
        }
    }

    public EnumFacing GetFacing(IBlockState state) {
        return ((EnumFacing)state.getValue(FACING));
    }

    private void updatePoweredState(World worldIn, BlockPos pos, IBlockState state)
    {
        boolean flag = ((Boolean)state.getValue(POWERED)).booleanValue();
        boolean flag1 = false;
        boolean didWork = false;

        Vec3i facing = ((EnumFacing)state.getValue(FACING)).getDirectionVec();

        List<EntityMinecart> list = this.<EntityMinecart>findMinecarts(worldIn, pos.add(facing), EntityMinecart.class);

        if (!list.isEmpty())
        {
            flag1 = false;
        }

        if (!list.isEmpty() && list.get(0) instanceof EntityMinecartChest) {
            EntityMinecartChest chestCart = (EntityMinecartChest) list.get(0);
            TileEntityCartEnergyLoader tileentity = (TileEntityCartEnergyLoader)worldIn.getTileEntity(pos);

            java.util.Set<String> tags = chestCart.getTags();
            if (tags.stream().filter(t -> t.startsWith("Energy")).count() == 0) {
                chestCart.addTag("Energy:0");
                chestCart.addTag("MaxEnergy:200000");
            }

            String energyTag =  tags.stream().filter(t -> t.startsWith("Energy")).findFirst().get();
            String maxEnergyTag =  tags.stream().filter(t -> t.startsWith("MaxEnergy")).findFirst().get();

            //read
            int currEnergy = 0;
            int maxEnergy = 0;
            int freeEnergy = 0;
            currEnergy = Integer.parseInt(energyTag.split(":")[1]);
            maxEnergy = Integer.parseInt(maxEnergyTag.split(":")[1]);
            freeEnergy = maxEnergy - currEnergy;

            if (freeEnergy != 0) {
                //WORK
                if (freeEnergy > 8000) freeEnergy = 8000;
                int amountToTransfer = tileentity.extractEnergy(GetFacing(state).getOpposite(), freeEnergy, false);
                tileentity.markDirty();
                currEnergy = currEnergy + amountToTransfer;
                freeEnergy = maxEnergy - currEnergy;
                didWork = true;

                if (amountToTransfer == 0) {
                    //Empty Buffer
                    flag1 = true;
                }

                //Save
                chestCart.removeTag(energyTag);
                chestCart.addTag("Energy:" + currEnergy);
                chestCart.setCustomNameTag("Energy: " + currEnergy + "/" + maxEnergy);

                //Protect it
                chestCart.setLockCode(new LockCode("Energy"));
                chestCart.dropContentsWhenDead = false;
                chestCart.setEntityInvulnerable(true);

                NBTTagCompound NBT = chestCart.getEntityData();

            }

            if (freeEnergy != 0) {
                //More space
            } else {
                //Full, Time to go
                flag1 = true;
            }

        } else if (!list.isEmpty() && !(list.get(0) instanceof EntityMinecartChest)) {
            //Go away
            flag1 = true;
        }


        if (flag1 && !flag)
        {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 3);
            worldIn.notifyNeighborsOfStateChange(pos, this, false);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this, false);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
        }

        if (!flag1 && flag)
        {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 3);
            worldIn.notifyNeighborsOfStateChange(pos, this, false);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this, false);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
        }

        if (flag1 || didWork)
        {
            worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
        }

        worldIn.updateComparatorOutputLevel(pos, this);
    }

    protected <T extends EntityMinecart> List<T> findMinecarts(World worldIn, BlockPos pos, Class<T> clazz, Predicate<Entity>... filter)
    {
        AxisAlignedBB axisalignedbb = this.getDectectionBox(pos);
        return filter.length != 1 ? worldIn.getEntitiesWithinAABB(clazz, axisalignedbb) : worldIn.getEntitiesWithinAABB(clazz, axisalignedbb, filter[0]);
    }

    private AxisAlignedBB getDectectionBox(BlockPos pos)
    {
        float f = 0.2F;
        return new AxisAlignedBB((double)((float)pos.getX() + 0.2F), (double)pos.getY(), (double)((float)pos.getZ() + 0.2F), (double)((float)(pos.getX() + 1) - 0.2F), (double)((float)(pos.getY() + 1) - 0.2F), (double)((float)(pos.getZ() + 1) - 0.2F));
    }


    @Override
    public boolean PreInit(FMLPreInitializationEvent event) {
        this.setUnlocalizedName(event.getModMetadata().modId + "." + "cartenergyloader");
        this.setRegistryName("cartenergyloader");
        ForgeRegistries.BLOCKS.register(this);

        itemBlock = new ItemBlock(this);
        itemBlock.setUnlocalizedName(event.getModMetadata().modId + "." + "cartenergyloader");
        itemBlock.setRegistryName(this.getRegistryName());

        ForgeRegistries.ITEMS.register(itemBlock);

        ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(this);
        //GameRegistry.registerTileEntity(TileEntityCartEnergyLoader.class, loc.toString());
        TileEntityCartEnergyLoader.register(loc.toString(), TileEntityCartEnergyLoader.class);

        this.setCreativeTab(RealmsOfAvalonMod.modTab);
        itemBlock.setCreativeTab(RealmsOfAvalonMod.modTab);
        return false;
    }

    @Override
    public boolean Initialize(FMLInitializationEvent event) {

        addRecipes();

        return false;
    }

    @Override
    public void RegisterRendering(FMLPreInitializationEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemBlock, 0, new ModelResourceLocation(itemBlock.getRegistryName(), "inventory"));
    }

    private void addRecipes() {


    }


    public static ItemBlock itemBlock;
}
