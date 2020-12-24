package net.petercashel.RealmsOfAvalonMod;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.petercashel.RealmsOfAvalonMod.Blocks.Detectors.BlockCartDetector;
import net.petercashel.RealmsOfAvalonMod.Init.BlockInit;
import net.petercashel.RealmsOfAvalonMod.Init.ItemInit;
import net.petercashel.RealmsOfAvalonMod.Proxy.IProxy;
import org.apache.logging.log4j.Logger;

@Mod(modid = RealmsOfAvalonMod.MODID, name = RealmsOfAvalonMod.NAME, version = RealmsOfAvalonMod.VERSION)
@Mod.EventBusSubscriber
public class RealmsOfAvalonMod
{
    public static final String MODID = "realmsofavalonmod";
    public static final String NAME = "Realms of Avalon Server Content Mod";
    public static final String VERSION = "1.0.2.4";

    private static Logger logger;

    public static CreativeTabs modTab = new CreativeTabs(CreativeTabs.getNextID(), "realmsofavalon.creativetab") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(BlockCartDetector.itemBlock);
        }
    };

    @SidedProxy(
            clientSide="net.petercashel.RealmsOfAvalonMod.Proxy.ClientProxy",
            serverSide="net.petercashel.RealmsOfAvalonMod.Proxy.ServerProxy"
    )
    public static IProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(RealmsOfAvalonEventHandler.class);

        BlockInit.PreInit(event);
        ItemInit.PreInit(event);
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        BlockInit.Init(event);
        ItemInit.Init(event);
        proxy.init(event);
    }

    /**
     * Post-Initialization FML Life Cycle event handling method which is automatically
     * called by Forge. It must be annotated as an event handler.
     *
     * @param event the event
     */
    @EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }


    /**
     * Fml life cycle.
     *
     * @param event the event
     */
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        proxy.serverStarting(event);
    }

    public static BlockCartDetector blockCartDetector = new BlockCartDetector();
    public static Item blockCartDetectorItem = new ItemBlock(blockCartDetector);

    @SubscribeEvent
    @EventHandler
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry registry = event.getRegistry();
        logger.info("Registering Blocks");
        //event.getRegistry().register(blockCartDetector);
    }

    @SubscribeEvent
    @EventHandler
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry registry = event.getRegistry();
        logger.info("Registering Items");
        //blockCartDetectorItem = blockCartDetectorItem.setRegistryName(blockCartDetector.getRegistryName());
        //event.getRegistry().register(blockCartDetectorItem);
    }
}
