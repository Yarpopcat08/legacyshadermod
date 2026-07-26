package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.ShadersTex;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.TextureManager;
import net.minecraft.src.TextureObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TextureManager.class)
public class TextureManagerMixin {
    @Inject(
            method = "bindTexture",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureUtil;bindTexture(I)V"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void onBindTexture(ResourceLocation id, CallbackInfo ci, Object var2) {
        ShadersTex.bindTexture((TextureObject) var2);
        ci.cancel();
    }
}
