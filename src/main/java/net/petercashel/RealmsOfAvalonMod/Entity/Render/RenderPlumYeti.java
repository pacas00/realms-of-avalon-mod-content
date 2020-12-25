package net.petercashel.RealmsOfAvalonMod.Entity.Render;

import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.petercashel.RealmsOfAvalonMod.Entity.EntityPlumYeti;
import net.petercashel.RealmsOfAvalonMod.Entity.Model.ModelPlumYeti;
import net.petercashel.RealmsOfAvalonMod.RealmsOfAvalonMod;

import javax.annotation.Nonnull;

public class RenderPlumYeti extends RenderLiving<EntityPlumYeti> {
    private ResourceLocation mobTexture = new ResourceLocation(  RealmsOfAvalonMod.MODID + ":textures/entity/plumyeti.png");

    public static final Factory FACTORY = new Factory();

    public RenderPlumYeti(RenderManager rendermanagerIn) {
        // We use the vanilla zombie model here and we simply
        // retexture it. Of course you can make your own model
        super(rendermanagerIn, new ModelPlumYeti(), 0.5F);
    }

    @Override
    @Nonnull
    protected ResourceLocation getEntityTexture(@Nonnull EntityPlumYeti entity) {
        return mobTexture;
    }

    public static class Factory implements IRenderFactory<EntityPlumYeti> {

        @Override
        public Render<? super EntityPlumYeti> createRenderFor(RenderManager manager) {
            return new RenderPlumYeti(manager);
        }

    }
}
