package net.petercashel.RealmsOfAvalonMod.GUI.Servers;


import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.network.LanServerInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ServerSelectionListPack extends GuiListExtended
{
    private final GuiMultiplayerPack owner;
    private final List<ServerListEntryNormalPack> serverListInternet = Lists.<ServerListEntryNormalPack>newArrayList();
    private final GuiListExtended.IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
    private int selectedSlotIndex = -1;

    public ServerSelectionListPack(GuiMultiplayerPack ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
    {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = ownerIn;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiListExtended.IGuiListEntry getListEntry(int index)
    {
        if (index < this.serverListInternet.size())
        {
            return this.serverListInternet.get(index);
        }
        else
        {
            index = index - this.serverListInternet.size();

            return this.serverListInternet.get(index);
        }
    }

    protected int getSize()
    {
        return this.serverListInternet.size();
    }

    public void setSelectedSlotIndex(int selectedSlotIndexIn)
    {
        this.selectedSlotIndex = selectedSlotIndexIn;
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == this.selectedSlotIndex;
    }

    public int getSelected()
    {
        return this.selectedSlotIndex;
    }

    public void updateOnlineServers(ServerListPack p_148195_1_)
    {
        this.serverListInternet.clear();

        for (int i = 0; i < p_148195_1_.countServers(); ++i)
        {
            this.serverListInternet.add(new ServerListEntryNormalPack(this.owner, p_148195_1_.getServerData(i)));
        }
    }

    public void updateNetworkServers(List<LanServerInfo> p_148194_1_)
    {
    }

    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 30;
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return super.getListWidth() + 85;
    }
}