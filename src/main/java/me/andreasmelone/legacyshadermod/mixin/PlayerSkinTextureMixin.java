package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.MultiTexID;
import me.andreasmelone.legacyshadermod.mixinif.IShaderTexture;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.PlayerSkinTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerSkinTexture.class)
public abstract class PlayerSkinTextureMixin extends AbstractTexture implements IShaderTexture {
    @Shadow
    private boolean field_6553;

    @Shadow
    public abstract int getGlId();

    @Override
    public MultiTexID shadermod$getMultiTexID() {
        if (!this.field_6553) {
            this.getGlId();
        }
        return this.shadermod$getOriginalMultiTexId();
    }
}
