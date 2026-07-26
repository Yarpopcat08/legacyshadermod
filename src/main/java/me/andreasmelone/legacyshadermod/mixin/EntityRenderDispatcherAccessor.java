package me.andreasmelone.legacyshadermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.src.RenderManager;

@Mixin(RenderManager.class)
public interface EntityRenderDispatcherAccessor {
    @Accessor("entityRenderMap")
    Map getRenderers();

    @Accessor("entityRenderMap")
    void setRenderers(Map map);
}
