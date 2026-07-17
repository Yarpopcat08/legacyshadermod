package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.mixinif.IShaderTexture;
import net.minecraft.client.texture.Texture;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Texture.class)
public interface TextureMixin extends IShaderTexture {
}
