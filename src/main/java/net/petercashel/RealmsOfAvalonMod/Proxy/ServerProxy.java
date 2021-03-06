package net.petercashel.RealmsOfAvalonMod.Proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.petercashel.RealmsOfAvalonMod.RealmsOfAvalonMod;

public class ServerProxy implements IProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(RealmsOfAvalonMod.instance, new GuiProxy_ContainerBasic());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event)
    {
        //event.registerServerCommand(new CommandStructureCapture());
    }

    @Override
    public EntityPlayer getPlayerEntityFromContext(MessageContext ctx)
    {
        return ctx.getServerHandler().player;
    }
}
