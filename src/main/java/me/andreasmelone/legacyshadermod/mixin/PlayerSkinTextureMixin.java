package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.MultiTexID;
import me.andreasmelone.legacyshadermod.mixinif.IShaderTexture;
import net.minecraft.src.AbstractTexture;
import net.minecraft.src.ThreadDownloadImageData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ThreadDownloadImageData.class)
public abstract class PlayerSkinTextureMixin extends AbstractTexture implements IShaderTexture {
    @Shadow
    private boolean textureUploaded;

    @Shadow
    public abstract int getGlTextureId();

    @Override
    public MultiTexID shadermod$getMultiTexID() {
        if (!this.textureUploaded) {
            this.getGlTextureId();
        }
        return this.shadermod$getOriginalMultiTexId();
    }
}
