package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.ShadersTex;
import net.minecraft.src.LayeredTexture;
import net.minecraft.src.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LayeredTexture.class)
public class LayeredTextureMixin {
    @Final
    @Shadow
    public List layeredTextureNames;

    @Inject(method = "loadTexture", at = @At("HEAD"), cancellable = true)
    private void onLoad(ResourceManager manager, CallbackInfo ci) {
        ShadersTex.loadLayeredTexture((LayeredTexture) (Object) this, manager, this.layeredTextureNames);
        ci.cancel();
    }
}
