package net.petercashel.RealmsOfAvalonMod.Proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.petercashel.RealmsOfAvalonMod.Entity.EntityPlumYeti;
import net.petercashel.RealmsOfAvalonMod.Entity.Render.RenderPlumYeti;
import net.petercashel.RealmsOfAvalonMod.Init.BlockInit;
import net.petercashel.RealmsOfAvalonMod.Init.EntityInit;
import net.petercashel.RealmsOfAvalonMod.Init.ItemInit;
import net.petercashel.RealmsOfAvalonMod.Init.SoundEventsInit;
import net.petercashel.RealmsOfAvalonMod.RealmsOfAvalonMod;

public class ClientProxy implements IProxy
{
    // mouse helper
    //public static MouseHelper mouseHelperAI = new MouseHelperAI(); // used to intercept user mouse movement for "bot" functionality

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        //Minecraft.getMinecraft().mouseHelper = ClientProxy.mouseHelperAI;
        //RenderFactories.registerEntityRenderers();

        SoundEventsInit.INSTANCE.RegisterRendering(event);
        BlockInit.RegisterRendering(event);
        ItemInit.RegisterRendering(event);
        EntityInit.INSTANCE.RegisterRendering(event);
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        // register key bindings
        //ModKeyBindings.registerKeyBindings();

        NetworkRegistry.INSTANCE.registerGuiHandler(RealmsOfAvalonMod.instance, new GuiProxy_ContainerBasic());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {

    }

    @Override
    public EntityPlayer getPlayerEntityFromContext(MessageContext ctx)
    {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().player : RealmsOfAvalonMod.proxy.getPlayerEntityFromContext(ctx));
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event)
    {
        // This will never get called on client side
    }
}
