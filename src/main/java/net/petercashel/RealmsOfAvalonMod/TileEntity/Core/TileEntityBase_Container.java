package net.petercashel.RealmsOfAvalonMod.TileEntity.Core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityBase_Container extends TileEntityBase_Directional implements IItemHandlerModifiable, IInventory {

    public TileEntityBase_Container(int InventorySize, boolean AllowExtraction, boolean AllowInsert) {
        super();
        this.InventorySize = InventorySize;
        stacks = NonNullList.<ItemStack>withSize(InventorySize, ItemStack.EMPTY);
        this.AllowExtraction = AllowExtraction;
        this.AllowInsert = AllowInsert;
    }

    private NonNullList<ItemStack> stacks = null;
    private int InventorySize = 1;
    public boolean AllowExtraction = false;
    public boolean AllowInsert = false;
    protected String customName;

    /**
     * Returns the number of slots in the inventory.
     */

    @Override
    public int getSizeInventory()
    {
        return stacks.size();
    }

    protected NonNullList<ItemStack> getItems()
    {
        return this.stacks;
    }

    @Override
    public boolean isEmpty()
    {
        for (ItemStack itemstack : this.stacks)
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Add the given ItemStack to this Dispenser. Return the Slot the Item was placed in or -1 if no free slot is
     * available.
     */
    public int addItemStack(ItemStack stack)
    {
        for (int i = 0; i < this.stacks.size(); ++i)
        {
            if (((ItemStack)this.stacks.get(i)).isEmpty())
            {
                this.setInventorySlotContents(i, stack);
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getSlots() {
        return getSizeInventory();
    }

    /**
     * Returns the stack in the given slot.
     */
    @Override
    public ItemStack getStackInSlot(int index)
    {
        try {
            return (ItemStack)this.getItems().get(index);
        } catch (Exception ex) {
            //Welp, that's back
            System.out.println(ex);
            return null;
        }
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack stackInSlot = this.getStackInSlot(slot);

        int m;
        if (!stackInSlot.isEmpty())
        {
            if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot)))
                return stack;

            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
                return stack;

            if (!isItemValidForSlot(slot, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

            if (stack.getCount() <= m)
            {
                if (!simulate)
                {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    setInventorySlotContents(slot, copy);
                    markDirty();
                }

                return ItemStack.EMPTY;
            }
            else
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    ItemStack copy = stack.splitStack(m);
                    copy.grow(stackInSlot.getCount());
                    setInventorySlotContents(slot, copy);
                    markDirty();
                    return stack;
                }
                else
                {
                    stack.shrink(m);
                    return stack;
                }
            }
        }
        else
        {
            if (!isItemValidForSlot(slot, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.getCount())
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    setInventorySlotContents(slot, stack.splitStack(m));
                    markDirty();
                    return stack;
                }
                else
                {
                    stack.shrink(m);
                    return stack;
                }
            }
            else
            {
                if (!simulate)
                {
                    setInventorySlotContents(slot, stack);
                    markDirty();
                }
                return ItemStack.EMPTY;
            }
        }

    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack stackInSlot = getStackInSlot(slot);

        if (stackInSlot.isEmpty())
            return ItemStack.EMPTY;

        if (simulate)
        {
            if (stackInSlot.getCount() < amount)
            {
                return stackInSlot.copy();
            }
            else
            {
                ItemStack copy = stackInSlot.copy();
                copy.setCount(amount);
                return copy;
            }
        }
        else
        {
            int m = Math.min(stackInSlot.getCount(), amount);

            ItemStack decrStackSize = decrStackSize(slot, m);
            markDirty();
            return decrStackSize;
        }
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        setInventorySlotContents(slot, stack);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return getInventoryStackLimit();
    }


    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = ItemStackHelper.getAndSplit(this.getItems(), index, count);

        if (!itemstack.isEmpty())
        {
            this.markDirty();
        }

        return itemstack;
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(this.getItems(), index);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack)
    {
        this.getItems().set(index, stack);

        if (stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }

        this.markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        int constSize = InventorySize;

        if (compound.getInteger("InventorySize") != 0) {
            this.InventorySize = compound.getInteger("InventorySize");
        }
        this.stacks = NonNullList.<ItemStack>withSize(this.InventorySize, ItemStack.EMPTY);

        ItemStackHelper.loadAllItems(compound, this.stacks);

        if (constSize != InventorySize) {
            NonNullList<ItemStack> newStacks = NonNullList.<ItemStack>withSize(constSize, ItemStack.EMPTY);
            for (int i = 0; i < InventorySize; i++) {
                if (i < constSize) {
                    newStacks.set(i, stacks.get(i));
                }
            }
            stacks = newStacks;
            InventorySize = constSize;
        }

        AllowExtraction = compound.getBoolean("AllowExtraction");
        AllowInsert = compound.getBoolean("AllowInsert");

        if (compound.hasKey("CustomName", 8))
        {
            this.customName = compound.getString("CustomName");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("InventorySize", InventorySize);

        ItemStackHelper.saveAllItems(compound, this.stacks);

        compound.setBoolean("AllowExtraction", AllowExtraction);
        compound.setBoolean("AllowInsert", AllowInsert);

        if (this.hasCustomName())
        {
            compound.setString("CustomName", this.customName);
        }

        return compound;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        if (this.world.getTileEntity(this.pos) != this)
        {
            return false;
        }
        else
        {
            return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
     * guis use Slot.isItemValid
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
       //this.getItems().clear();
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && Facing != null && Facing == facing.getOpposite()) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, final EnumFacing from) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && Facing != null && Facing == from.getOpposite()) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this);
        }
        return super.getCapability(capability, from);
    }


    public int getFirstFilledItemSlot() {
        for (int i = 0; i < this.getItems().size(); i++) {
            if (!this.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public int getFirstEmptyItemSlot() {
        for (int i = 0; i < this.getItems().size(); i++) {
            if (this.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container.dispenser";
    }

    @Override
    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return this.customName != null && !this.customName.isEmpty();
    }

    public void setCustomName(String p_190575_1_)
    {
        this.customName = p_190575_1_;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public ITextComponent getDisplayName()
    {
        return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
    }

    public String getGuiID()
    {
        return "minecraft:dispenser";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerDispenser(playerInventory, this);
    }


    public IInventory GetInventory() {
        return (IInventory) this;
    }
}
