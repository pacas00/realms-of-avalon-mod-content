package net.petercashel.RealmsOfAvalonMod.Init;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.petercashel.RealmsOfAvalonMod.Blocks.*;
import net.petercashel.RealmsOfAvalonMod.Interfaces.IInitEvents;

import java.util.ArrayList;

public class BlockInit {

    public static final BlockInit INSTANCE = new BlockInit();

    public static void PreInit(FMLPreInitializationEvent event) {

        blockCartDetector = new BlockCartDetector();
        blockCartDetectorItems = new BlockCartDetectorItems();
        blockCartDetectorFluids = new BlockCartDetectorFluids();
        blockCartDetectorEnergy = new BlockCartDetectorEnergy();

        blockCartEnergyLoader = new BlockCartEnergyLoader();
        blockCartEnergyUnLoader = new BlockCartEnergyUnLoader();

        blockCartFluidLoader = new BlockCartFluidLoader();
        blockCartFluidUnLoader = new BlockCartFluidUnLoader();

        blockCartItemLoader = new BlockCartItemLoader();
        blockCartItemUnLoader = new BlockCartItemUnLoader();

        initList.add(blockCartDetector);
        initList.add(blockCartDetectorItems);
        initList.add(blockCartDetectorFluids);
        initList.add(blockCartDetectorEnergy);

        initList.add(blockCartEnergyLoader);
        initList.add(blockCartEnergyUnLoader);

        initList.add(blockCartFluidLoader);
        initList.add(blockCartFluidUnLoader);

        initList.add(blockCartItemLoader);
        initList.add(blockCartItemUnLoader);

        for (IInitEvents init : initList) {
            init.PreInit(event);
        }
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    public static void Init(FMLInitializationEvent event) {
        for (IInitEvents init : initList) {
            init.Initialize(event);
        }
    }

    public static void RegisterRendering(FMLPreInitializationEvent event) {
        for (IInitEvents init : initList) {
            init.RegisterRendering(event);
        }
    }


    private static ArrayList<IInitEvents> initList = new ArrayList<>();

    /* REFERENCES */
    public static BlockCartDetector blockCartDetector;
    public static BlockCartDetectorItems blockCartDetectorItems;
    public static BlockCartDetectorFluids blockCartDetectorFluids;
    public static BlockCartDetectorEnergy blockCartDetectorEnergy;


    public static BlockCartEnergyLoader blockCartEnergyLoader;
    public static BlockCartEnergyUnLoader blockCartEnergyUnLoader;

    public static BlockCartFluidLoader blockCartFluidLoader;
    public static BlockCartFluidUnLoader blockCartFluidUnLoader;

    public static BlockCartItemLoader blockCartItemLoader;
    public static BlockCartItemUnLoader blockCartItemUnLoader;

}
