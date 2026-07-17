package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.ShadersTex;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TextureManager.class)
public class TextureManagerMixin {
    @Inject(
            method = "bindTexture",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureUtil;bindTexture(I)V"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void onBindTexture(Identifier id, CallbackInfo ci, Object var2) {
        ShadersTex.bindTexture((Texture) var2);
        ci.cancel();
    }
}
