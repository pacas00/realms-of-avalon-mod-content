package net.petercashel.RealmsOfAvalonMod.Init;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.petercashel.RealmsOfAvalonMod.Interfaces.IInitEvents;

import java.util.ArrayList;

public class ItemInit {

    public static final ItemInit INSTANCE = new ItemInit();

    public static void PreInit(FMLPreInitializationEvent event) {

       // blockCartDetector = new BlockCartDetector();


        //initList.add(blockCartDetector);


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
    //public static BlockCartDetector blockCartDetector;
}
