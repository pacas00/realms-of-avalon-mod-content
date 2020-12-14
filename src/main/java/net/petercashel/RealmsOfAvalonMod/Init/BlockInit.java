package net.petercashel.RealmsOfAvalonMod.Init;

import net.minecraftforge.common.MinecraftForge;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartDetector;
import net.petercashel.RealmsOfAvalonMod.Blocks.BlockCartEnergyLoader;
import net.petercashel.RealmsOfAvalonMod.Interfaces.IInitEvents;

import java.util.ArrayList;

public class BlockInit {

    public static final BlockInit INSTANCE = new BlockInit();

    public static void PreInit() {

        blockCartDetector = new BlockCartDetector();
        blockCartEnergyLoader = new BlockCartEnergyLoader();

        initList.add(blockCartDetector);
        initList.add(blockCartEnergyLoader);


        for (IInitEvents init : initList) {
            init.PreInit();
        }
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    public static void Init() {
        for (IInitEvents init : initList) {
            init.Initialize();
        }
    }


    private static ArrayList<IInitEvents> initList = new ArrayList<>();

    /* REFERENCES */
    public static BlockCartDetector blockCartDetector;
    public static BlockCartEnergyLoader blockCartEnergyLoader;
}
