package net.petercashel.RealmsOfAvalonMod.Blocks;

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
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.petercashel.RealmsOfAvalonMod.Interfaces.IInitEvents;
import net.petercashel.RealmsOfAvalonMod.RealmsOfAvalonMod;
import net.petercashel.RealmsOfAvalonMod.TileEntity.TileEntityCartFluidUnLoader;

import java.util.List;

public class BlockCartFluidUnLoader extends BlockCartLoaderBase implements IInitEvents, ITileEntityProvider {

    public BlockCartFluidUnLoader() {
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

            if (tileentity instanceof TileEntityCartFluidUnLoader)
            {
                playerIn.sendStatusMessage(new TextComponentString("Stored: " + ((TileEntityCartFluidUnLoader)tileentity).getFluidStored(EnumFacing.SOUTH) + "/" + ((TileEntityCartFluidUnLoader)tileentity).getMaxFluidStored(EnumFacing.SOUTH)), true);
            }

            return true;
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityCartFluidUnLoader();
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

            if (tileentity instanceof TileEntityCartFluidUnLoader)
            {
                //((TileEntityCartFluidUnLoader)tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityCartFluidUnLoader)
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
            TileEntityCartFluidUnLoader tileentity = (TileEntityCartFluidUnLoader)worldIn.getTileEntity(pos);

            //Get Inventory of cart
            //Find items that store fluid
            //Try to empty first full one

            int slots = chestCart.getSizeInventory();
            boolean Transferedfluid = false;
            flag1 = true;

            for (int i = 0; i < slots; i++) {
                ItemStack slot = chestCart.getStackInSlot(i);
                if (i == slots - 1) {
                    //Last slot. Always set going here if null or full.
                    if (slot == null || slot.isEmpty()) flag1 = true;
                }
                if (slot == null) {
                    continue;
                }
                if (slot.isEmpty()) {
                    continue;
                }

                if (!slot.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                    continue;
                }
                IFluidHandlerItem item = slot.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

                if (item == null || item.getTankProperties() == null || item.getTankProperties().length == 0) {
                    continue;
                }

                net.minecraftforge.fluids.capability.IFluidTankProperties[] props = item.getTankProperties();

                int currFluiditem = (props[0].getContents() != null ? props[0].getContents().amount : 0);
                int maxFluiditem = props[0].getCapacity();

                //Check we can store this here
                if (!tileentity.getTank().canFillFluidType(props[0].getContents())) {
                    continue;
                }

                int totalTransfered = 0;
                for (int j = 0; j < 1; j++) {
                    if (currFluiditem == 0) continue;
                    int amountToTransfer = tileentity.TransferRate;
                    FluidStack fluidStack = item.drain(amountToTransfer, false);
                    amountToTransfer = tileentity.getTank().fill(fluidStack, false);

                    fluidStack = item.drain(amountToTransfer, true);
                    totalTransfered += tileentity.getTank().fill(fluidStack, true);
                }

                if (totalTransfered != 0) {
                    //We transfered something this tick. Don't lock the system up.
                    Transferedfluid = true;
                    break;
                }

                currFluiditem = (props[0].getContents() != null ? props[0].getContents().amount : 0);

                if (i == slots - 1) {
                    //Last slot. Always set going here if null or empty.
                    if (slot == null || slot.isEmpty()) flag1 = true;
                    if (currFluiditem == 0) flag1 = true;
                    if (totalTransfered == 0) flag1 = true;
                }

            }

            if (Transferedfluid) {
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
        this.setUnlocalizedName(event.getModMetadata().modId + "." + "cartfluidunloader");
        this.setRegistryName("cartfluidunloader");
        ForgeRegistries.BLOCKS.register(this);

        itemBlock = new ItemBlock(this);
        itemBlock.setUnlocalizedName(event.getModMetadata().modId + "." + "cartfluidunloader");
        itemBlock.setRegistryName(this.getRegistryName());

        ForgeRegistries.ITEMS.register(itemBlock);

        ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(this);
        //GameRegistry.registerTileEntity(TileEntityCartFluidUnLoader.class, loc.toString());
        TileEntityCartFluidUnLoader.register(loc.toString(), TileEntityCartFluidUnLoader.class);

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
