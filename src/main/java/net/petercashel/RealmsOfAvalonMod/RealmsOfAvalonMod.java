package net.petercashel.RealmsOfAvalonMod;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartDetector;
import net.petercashel.RealmsOfAvalonMod.Init.BlockInit;
import net.petercashel.RealmsOfAvalonMod.Init.ItemInit;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

@Mod(modid = RealmsOfAvalonMod.MODID, name = RealmsOfAvalonMod.NAME, version = RealmsOfAvalonMod.VERSION)
public class RealmsOfAvalonMod
{
    public static final String MODID = "realmsofavalonmod";
    public static final String NAME = "Realms of Avalon Server Content Mod";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);

        BlockInit.PreInit();
        ItemInit.PreInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

        BlockInit.Init();
        ItemInit.Init();
    }

    public static BlockCartDetector blockCartDetector = new BlockCartDetector();
    public static Item blockCartDetectorItem = new ItemBlock(blockCartDetector);

    @SubscribeEvent
    @EventHandler
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry registry = event.getRegistry();
        logger.info("Registering Blocks");
        event.getRegistry().register(blockCartDetector);
    }

    @SubscribeEvent
    @EventHandler
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry registry = event.getRegistry();
        logger.info("Registering Items");
        blockCartDetectorItem = blockCartDetectorItem.setRegistryName(blockCartDetector.getRegistryName());
        event.getRegistry().register(blockCartDetectorItem);
    }
}
