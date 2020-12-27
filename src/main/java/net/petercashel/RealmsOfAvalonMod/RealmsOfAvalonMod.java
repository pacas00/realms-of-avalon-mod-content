package net.petercashel.RealmsOfAvalonMod;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.petercashel.RealmsOfAvalonMod.Blocks.Detectors.BlockCartDetector;
import net.petercashel.RealmsOfAvalonMod.Entity.EntityPlumYeti;
import net.petercashel.RealmsOfAvalonMod.Init.BlockInit;
import net.petercashel.RealmsOfAvalonMod.Init.EntityInit;
import net.petercashel.RealmsOfAvalonMod.Init.ItemInit;
import net.petercashel.RealmsOfAvalonMod.Init.SoundEventsInit;
import net.petercashel.RealmsOfAvalonMod.Proxy.IProxy;
import org.apache.logging.log4j.Logger;

@Mod(modid = RealmsOfAvalonMod.MODID, name = RealmsOfAvalonMod.NAME, version = RealmsOfAvalonMod.VERSION, dependencies = RealmsOfAvalonMod.DEPENDENCIES, certificateFingerprint = RealmsOfAvalonMod.PubKey)
@Mod.EventBusSubscriber
public class RealmsOfAvalonMod
{
    public static final String MODID = "realmsofavalonmod";
    public static final String NAME = "Realms of Avalon Server Content Mod";
    public static final String VERSION = "1.0.2.4";
    public static final String PubKey = "b205ed572671b4a10fccb26ccd808612a5bfefdc";

    //forge,cofhcore,redstoneflux,thermalfoundation,thermalexpansion
    public static final String DEPENDENCIES = "required-after:forge@14.23.4.2718;required-after:cofhcore@4.6.0;required-after:redstoneflux@2.1.0;required-after:thermalfoundation@2.6.0;required-after:thermalexpansion@5.5.0";

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

    @Mod.Instance
    public static RealmsOfAvalonMod instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(RealmsOfAvalonEventHandler.class);

        SoundEventsInit.INSTANCE.PreInit(event);
        BlockInit.PreInit(event);
        ItemInit.PreInit(event);
        EntityInit.INSTANCE.PreInit(event);

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        SoundEventsInit.INSTANCE.Initialize(event);
        BlockInit.Init(event);
        ItemInit.Init(event);
        EntityInit.INSTANCE.Initialize(event);

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

    public static boolean IsMagmaServer() {
        CrashReport crashreport = CrashReport.makeCrashReport(new Exception("Nothing"), "Nothing");
        if (crashreport.getCompleteReport().contains("Magma Version")) return true;
        return false;
    }

    @SubscribeEvent
    @EventHandler
    public static void onFingerPrintViolation(FMLFingerprintViolationEvent event)
    {
        /*
            TO THOSE READING THE SOURCE CODE

            Yeah. I know, this is a dick move to crash out like this. But I don't want to deal with unofficial builds or supporting them.


         */
        boolean IsMagma = IsMagmaServer();

        // Not running in a dev environment
        if (event.isDirectory() == false)
        {
            logger.warn("*********************************************************************************************");
            logger.warn("*****                                    WARNING                                        *****");
            logger.warn("*****                                                                                   *****");
            logger.warn("*****   The signature of the mod file '{}' does not match the expected fingerprint!     *****", event.getSource().getName());
            logger.warn("*****   This might mean that the mod file has been tampered with!                       *****");
            logger.warn("*****   If you did not download the mod {} directly from Curse/CurseForge,       *****", RealmsOfAvalonMod.NAME);
            logger.warn("*****   or using one of the well known launchers, and you did not                       *****");
            logger.warn("*****   modify the mod file at all yourself, then it's possible,                        *****");
            logger.warn("*****   that it may contain malware or other unwanted things!                           *****");
            logger.warn("*********************************************************************************************");


            //Check if it's a server running Magma
            if (IsMagma && RealmsOfAvalonMod.VERSION != "1.0.2.4") {
                //Running magma. Ok, that's fine I guess
            } else {
                //Unofficial builds are not cool and explode
                Throwable t = new RuntimeException("Unofficial builds are not supported. Unofficial builds will terminate minecraft. If you are running an Unofficial build, Please download an official build from CurseForge. I do not support Unofficial builds.");
                CrashReport crashreport = CrashReport.makeCrashReport(t, "Unofficial builds are not supported.");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("FMLFingerprintViolationEvent");
                throw new ReportedException(crashreport);
            }
        }
    }
}
