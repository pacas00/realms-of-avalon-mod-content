package net.petercashel.RealmsOfAvalonMod.GUI.Servers;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;

import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiMultiplayerPack extends GuiScreen
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServerPinger oldServerPinger = new ServerPinger();
    private final GuiScreen parentScreen;
    private ServerSelectionListPack serverListSelector;
    private ServerListPack savedServerList;
    private GuiButton btnSelectServer;
    private boolean addingServer;
    private boolean editingServer;
    private boolean directConnect;
    /** The text to be displayed when the player's cursor hovers over a server listing. */
    private String hoveringText;
    private ServerData selectedServer;
    private boolean initialized;

    public GuiMultiplayerPack(GuiScreen parentScreen)
    {
        this.parentScreen = parentScreen;
        net.minecraftforge.fml.client.FMLClientHandler.instance().setupServerList();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        if (this.initialized)
        {
            this.serverListSelector.setDimensions(this.width, this.height, 32, this.height - 36);
        }
        else
        {
            this.initialized = true;
            this.savedServerList = new ServerListPack(this.mc);
            this.savedServerList.loadServerList();

            this.serverListSelector = new ServerSelectionListPack(this, this.mc, this.width, this.height, 32, this.height - 36, 36);
            this.serverListSelector.updateOnlineServers(this.savedServerList);
        }

        this.createButtons();
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.serverListSelector.handleMouseInput();
    }

    public void createButtons()
    {
        this.btnSelectServer = this.addButton(new GuiButton(1, this.width / 2 - 154, this.height - 28, 100, 20, I18n.format("selectServer.select")));
        this.buttonList.add(new GuiButton(8, this.width / 2 - 50, this.height - 28, 100, 20, I18n.format("selectServer.refresh")));
        this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 50, this.height - 28, 100, 20, I18n.format("gui.cancel")));
        this.selectServer(this.serverListSelector.getSelected());
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();

        this.oldServerPinger.pingPendingNetworks();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);

        this.oldServerPinger.clearPendingNetworks();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.getSelected() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());

            if (button.id == 2 && guilistextended$iguilistentry instanceof ServerListEntryNormalPack)
            {
                String s4 = ((ServerListEntryNormalPack)guilistextended$iguilistentry).getServerData().serverName;

                if (s4 != null)
                {
                    String s = I18n.format("selectServer.deleteQuestion");
                    String s1 = "'" + s4 + "' " + I18n.format("selectServer.deleteWarning");
                    String s2 = I18n.format("selectServer.deleteButton");
                    String s3 = I18n.format("gui.cancel");
                    GuiYesNo guiyesno = new GuiYesNo(this, s, s1, s2, s3, this.serverListSelector.getSelected());
                    this.mc.displayGuiScreen(guiyesno);
                }
            }
            else if (button.id == 1)
            {
                this.connectToSelected();
            }
            else if (button.id == 4)
            {
                this.directConnect = true;
                this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false);
                this.mc.displayGuiScreen(new GuiScreenServerList(this, this.selectedServer));
            }
            else if (button.id == 3)
            {
                this.addingServer = true;
                this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false);
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer));
            }
            else if (button.id == 7 && guilistextended$iguilistentry instanceof ServerListEntryNormalPack)
            {
                this.editingServer = true;
                ServerData serverdata = ((ServerListEntryNormalPack)guilistextended$iguilistentry).getServerData();
                this.selectedServer = new ServerData(serverdata.serverName, serverdata.serverIP, false);
                this.selectedServer.copyFrom(serverdata);
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer));
            }
            else if (button.id == 0)
            {
                this.mc.displayGuiScreen(this.parentScreen);
            }
            else if (button.id == 8)
            {
                this.refreshServerList();
            }
        }
    }

    private void refreshServerList()
    {
        this.mc.displayGuiScreen(new GuiMultiplayerPack(this.parentScreen));
    }

    public void confirmClicked(boolean result, int id)
    {
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.getSelected() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());

        if (this.directConnect)
        {
            this.directConnect = false;

            if (result)
            {
                this.connectToServer(this.selectedServer);
            }
            else
            {
                this.mc.displayGuiScreen(this);
            }
        }
        else if (this.addingServer)
        {
            this.addingServer = false;

            if (result)
            {
                this.savedServerList.addServerData(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.updateOnlineServers(this.savedServerList);
            }

            this.mc.displayGuiScreen(this);
        }
        else if (this.editingServer)
        {
            this.editingServer = false;

            if (result && guilistextended$iguilistentry instanceof ServerListEntryNormalPack)
            {
                ServerData serverdata = ((ServerListEntryNormalPack)guilistextended$iguilistentry).getServerData();
                serverdata.serverName = this.selectedServer.serverName;
                serverdata.serverIP = this.selectedServer.serverIP;
                serverdata.copyFrom(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.updateOnlineServers(this.savedServerList);
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        int i = this.serverListSelector.getSelected();
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = i < 0 ? null : this.serverListSelector.getListEntry(i);

        if (keyCode == 63)
        {
            this.refreshServerList();
        }
        else
        {
            if (i >= 0)
            {
                if (keyCode == 200)
                {
                    if (isShiftKeyDown())
                    {
                        if (i > 0 && guilistextended$iguilistentry instanceof ServerListEntryNormalPack)
                        {
                            this.savedServerList.swapServers(i, i - 1);
                            this.selectServer(this.serverListSelector.getSelected() - 1);
                            this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                            this.serverListSelector.updateOnlineServers(this.savedServerList);
                        }
                    }
                    else if (i > 0)
                    {
                        this.selectServer(this.serverListSelector.getSelected() - 1);
                        this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                    }
                    else
                    {
                        this.selectServer(-1);
                    }
                }
                else if (keyCode == 208)
                {
                    if (isShiftKeyDown())
                    {
                        if (i < this.savedServerList.countServers() - 1)
                        {
                            this.savedServerList.swapServers(i, i + 1);
                            this.selectServer(i + 1);
                            this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                            this.serverListSelector.updateOnlineServers(this.savedServerList);
                        }
                    }
                    else if (i < this.serverListSelector.getSize())
                    {
                        this.selectServer(this.serverListSelector.getSelected() + 1);
                        this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                    }
                    else
                    {
                        this.selectServer(-1);
                    }
                }
                else if (keyCode != 28 && keyCode != 156)
                {
                    super.keyTyped(typedChar, keyCode);
                }
                else
                {
                    this.actionPerformed(this.buttonList.get(2));
                }
            }
            else
            {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.hoveringText = null;
        this.drawDefaultBackground();
        this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, I18n.format("gui.realmsofavalonmod.servers"), this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.hoveringText != null)
        {
            this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
        }
    }

    public void connectToSelected()
    {
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.getSelected() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());

        if (guilistextended$iguilistentry instanceof ServerListEntryNormalPack)
        {
            this.connectToServer(((ServerListEntryNormalPack)guilistextended$iguilistentry).getServerData());
        }
    }

    private void connectToServer(ServerData server)
    {
        net.minecraftforge.fml.client.FMLClientHandler.instance().connectToServer(this, server);
    }

    public void selectServer(int index)
    {
        this.serverListSelector.setSelectedSlotIndex(index);
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = index < 0 ? null : this.serverListSelector.getListEntry(index);
        this.btnSelectServer.enabled = false;
        //this.btnEditServer.enabled = false;

        if (guilistextended$iguilistentry != null && !(guilistextended$iguilistentry instanceof ServerListEntryLanScan))
        {
            this.btnSelectServer.enabled = true;

            if (guilistextended$iguilistentry instanceof ServerListEntryNormalPack)
            {
                //this.btnEditServer.enabled = true;
            }
        }
    }

    public ServerPinger getOldServerPinger()
    {
        return this.oldServerPinger;
    }

    public void setHoveringText(String p_146793_1_)
    {
        this.hoveringText = p_146793_1_;
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverListSelector.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
        this.serverListSelector.mouseReleased(mouseX, mouseY, state);
    }

    public ServerListPack getServerList()
    {
        return this.savedServerList;
    }

    public boolean canMoveUp(ServerListEntryNormalPack p_175392_1_, int p_175392_2_)
    {
        return p_175392_2_ > 0;
    }

    public boolean canMoveDown(ServerListEntryNormalPack p_175394_1_, int p_175394_2_)
    {
        return p_175394_2_ < this.savedServerList.countServers() - 1;
    }

    public void moveServerUp(ServerListEntryNormalPack p_175391_1_, int p_175391_2_, boolean p_175391_3_)
    {
        int i = p_175391_3_ ? 0 : p_175391_2_ - 1;
        this.savedServerList.swapServers(p_175391_2_, i);

        if (this.serverListSelector.getSelected() == p_175391_2_)
        {
            this.selectServer(i);
        }

        this.serverListSelector.updateOnlineServers(this.savedServerList);
    }

    public void moveServerDown(ServerListEntryNormalPack p_175393_1_, int p_175393_2_, boolean p_175393_3_)
    {
        int i = p_175393_3_ ? this.savedServerList.countServers() - 1 : p_175393_2_ + 1;
        this.savedServerList.swapServers(p_175393_2_, i);

        if (this.serverListSelector.getSelected() == p_175393_2_)
        {
            this.selectServer(i);
        }

        this.serverListSelector.updateOnlineServers(this.savedServerList);
    }
}