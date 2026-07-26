package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.mixinif.IShaderTexture;
import net.minecraft.src.TextureObject;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TextureObject.class)
public interface TextureMixin extends IShaderTexture {
}
