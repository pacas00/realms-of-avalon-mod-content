package net.petercashel.RealmsOfAvalonMod.Blocks.Loaders;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.ItemHandlerHelper;
import net.petercashel.RealmsOfAvalonMod.Blocks.Core.BlockCartLoaderBase;
import net.petercashel.RealmsOfAvalonMod.Interfaces.IInitEvents;
import net.petercashel.RealmsOfAvalonMod.RealmsOfAvalonMod;
import net.petercashel.RealmsOfAvalonMod.TileEntity.Loaders.TileEntityCartItemLoader;

import java.util.List;

public class BlockCartItemLoader extends BlockCartLoaderBase implements IInitEvents, ITileEntityProvider {

    public BlockCartItemLoader() {
        super(Material.ROCK);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.requiresUpdates();
        this.needsRandomTick = true;
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

            if (tileentity instanceof TileEntityCartItemLoader)
            {
                //playerIn.displayGUIChest((TileEntityCartItemLoader)tileentity);
                playerIn.openGui(RealmsOfAvalonMod.instance, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());
                //playerIn.sendStatusMessage(new TextComponentString("Stored: " + ((TileEntityCartItemLoader)tileentity).getFluidStored(EnumFacing.SOUTH) + "/" + ((TileEntityCartItemLoader)tileentity).getMaxFluidStored(EnumFacing.SOUTH)), true);
            }

            return true;
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityCartItemLoader();
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

            if (tileentity instanceof TileEntityCartItemLoader)
            {
                //((TileEntityCartItemLoader)tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityCartItemLoader)
        {
//            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityDispenser)tileentity);
//            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void updatePoweredState(World worldIn, BlockPos pos, IBlockState state)
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
            TileEntityCartItemLoader tileentity = (TileEntityCartItemLoader)worldIn.getTileEntity(pos);

            //Get Inventory of cart
            //Find items that store energy
            //Try to fill first non-full one

            int slots = chestCart.getSizeInventory();
            boolean TransferedItems = false;
            flag1 = true;

            for (int i = 0; i < slots; i++) {
                int tSlotId = tileentity.getFirstFilledItemSlot();
                if (tSlotId == -1) {
                    continue;
                }
                ItemStack tSlot = tileentity.getStackInSlot(tSlotId);
                ItemStack slot = chestCart.getStackInSlot(i);
                if (slot == null) {
                    continue;
                }

                if (slot.isEmpty()) {
                    //Transfer and Go!
                    ItemStack retStack = ItemHandlerHelper.insertItem(chestCart.itemHandler, tSlot, false);
                    tileentity.setStackInSlot(tSlotId, retStack);
                    TransferedItems = true;
                    break;
                }

                //Ok, so the slot isnt empty.

                //Option A: Don't try to stack items when a slot is full/partly full
                //Option B: Try to make it work.

            }

            if (TransferedItems) {
                flag1 = false;
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



    @Override
    public boolean PreInit(FMLPreInitializationEvent event) {
        this.setUnlocalizedName(event.getModMetadata().modId + "." + "cartitemloader");
        this.setRegistryName("cartitemloader");
        ForgeRegistries.BLOCKS.register(this);

        itemBlock = new ItemBlock(this);
        itemBlock.setUnlocalizedName(event.getModMetadata().modId + "." + "cartitemloader");
        itemBlock.setRegistryName(this.getRegistryName());

        ForgeRegistries.ITEMS.register(itemBlock);

        ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(this);
        //GameRegistry.registerTileEntity(TileEntityCartItemLoader.class, loc.toString());
        TileEntityCartItemLoader.register(loc.toString(), TileEntityCartItemLoader.class);

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
