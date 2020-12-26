package net.petercashel.RealmsOfAvalonMod.Init;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.petercashel.RealmsOfAvalonMod.Entity.EntityPlumYeti;
import net.petercashel.RealmsOfAvalonMod.Entity.Render.RenderPlumYeti;
import net.petercashel.RealmsOfAvalonMod.Interfaces.IInitEvents;
import net.petercashel.RealmsOfAvalonMod.RealmsOfAvalonMod;

public class EntityInit implements IInitEvents {
    public static final EntityInit INSTANCE = new EntityInit();


    @Override
    public boolean PreInit(FMLPreInitializationEvent event) {

        // Every entity in our mod has an ID (local to this mod)
        int id = 1;

        EntityRegistry.registerModEntity(new ResourceLocation(RealmsOfAvalonMod.MODID, "plumyeti"), EntityPlumYeti.class, "plumyeti", id++, RealmsOfAvalonMod.instance, 64, 3, true, 0xE9EDF0, 0x7C5A9B);

        // We want our mob to spawn in Plains and ice plains biomes. If you don't add this then it will not spawn automatically
        // but you can of course still make it spawn manually
        EntityRegistry.addSpawn(EntityPlumYeti.class, 100, 3, 5, EnumCreatureType.MONSTER, Biomes.ICE_MOUNTAINS, Biomes.ICE_PLAINS);

        // This is the loot table for our mob
        LootTableList.register(EntityPlumYeti.LOOT);

        return false;
    }

    @Override
    public boolean Initialize(FMLInitializationEvent event) {
        return false;
    }

    @Override
    public void RegisterRendering(FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityPlumYeti.class, RenderPlumYeti.FACTORY);
    }
}
