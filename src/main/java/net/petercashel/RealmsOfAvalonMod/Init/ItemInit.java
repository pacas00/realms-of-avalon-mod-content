package net.petercashel.RealmsOfAvalonMod.Init;

import net.minecraftforge.common.MinecraftForge;
import net.petercashel.RealmsOfAvalonMod.Interfaces.IInitEvents;

import java.util.ArrayList;

public class ItemInit {

    public static final ItemInit INSTANCE = new ItemInit();

    public static void PreInit() {

       // blockCartDetector = new BlockCartDetector();


        //initList.add(blockCartDetector);


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
    //public static BlockCartDetector blockCartDetector;
}
