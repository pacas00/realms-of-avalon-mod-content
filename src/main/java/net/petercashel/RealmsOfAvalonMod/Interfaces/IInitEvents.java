package net.petercashel.RealmsOfAvalonMod.Interfaces;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IInitEvents {
    boolean PreInit(FMLPreInitializationEvent event);

    boolean Initialize(FMLInitializationEvent event);

    void RegisterRendering(FMLPreInitializationEvent event);
}
