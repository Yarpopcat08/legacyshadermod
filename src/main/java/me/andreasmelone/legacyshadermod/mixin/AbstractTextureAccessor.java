package me.andreasmelone.legacyshadermod.mixin;

import net.minecraft.client.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractTexture.class)
public interface AbstractTextureAccessor {
    @Accessor("glId")
    int shadermod$getGlId();

    @Accessor("glId")
    void shadermod$setGlId(int id);
}
