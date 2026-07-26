package me.andreasmelone.legacyshadermod.mixin;

import net.minecraft.src.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractTexture.class)
public interface AbstractTextureAccessor {
    @Accessor("glTextureId")
    int shadermod$getGlId();

    @Accessor("glTextureId")
    void shadermod$setGlId(int id);
}
