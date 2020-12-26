package net.petercashel.RealmsOfAvalonMod.Init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.petercashel.RealmsOfAvalonMod.Interfaces.IInitEvents;
import net.petercashel.RealmsOfAvalonMod.RealmsOfAvalonMod;

public class SoundEventsInit implements IInitEvents {
    public static final SoundEventsInit INSTANCE = new SoundEventsInit();
    public static SoundEvent dodgemsMusic;

    @Override
    public boolean PreInit(FMLPreInitializationEvent event) {

        ResourceLocation dodgemsRecord = new ResourceLocation(RealmsOfAvalonMod.MODID, "record_dodgems");
        dodgemsMusic = new SoundEvent(dodgemsRecord);
        dodgemsMusic.setRegistryName(dodgemsRecord);
        ForgeRegistries.SOUND_EVENTS.register(dodgemsMusic);


        return false;
    }

    @Override
    public boolean Initialize(FMLInitializationEvent event) {
        return false;
    }

    @Override
    public void RegisterRendering(FMLPreInitializationEvent event) {

    }
}
