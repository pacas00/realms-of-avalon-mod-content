package net.petercashel.RealmsOfAvalonMod.Init;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.petercashel.RealmsOfAvalonMod.Items.ItemRecordExt;
import net.petercashel.RealmsOfAvalonMod.Interfaces.IInitEvents;

import java.util.ArrayList;

public class ItemInit {

    public static final ItemInit INSTANCE = new ItemInit();

    public static void PreInit(FMLPreInitializationEvent event) {

        for (IInitEvents init : initList) {
            init.PreInit(event);
        }
        MinecraftForge.EVENT_BUS.register(INSTANCE);


        //Records need manual registration
        itemRecordDodgems = new ItemRecordExt("dodgems", SoundEventsInit.dodgemsMusic).setUnlocalizedName("record").setRegistryName("record_dodgems");
        ForgeRegistries.ITEMS.register(itemRecordDodgems);
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

        //Records need manual registration
        ModelLoader.setCustomModelResourceLocation(itemRecordDodgems, 0, new ModelResourceLocation(itemRecordDodgems.getRegistryName(), "inventory"));
    }

    private static ArrayList<IInitEvents> initList = new ArrayList<>();


    /* REFERENCES */
    public static Item itemRecordDodgems;
}

