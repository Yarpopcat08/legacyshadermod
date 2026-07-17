package me.andreasmelone.legacyshadermod.mixin;

import com.mojang.blaze3d.platform.GLX;
import me.andreasmelone.legacyshadermod.client.GLXActiveTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GLX.class)
public class GLXMixin {
    @Inject(
            method = "gl13ActiveTexture",
            at = @At("HEAD")
    )
    private static void gl13ActiveTexture(int texture, CallbackInfo ci) {
        GLXActiveTexture.activeTexUnit = texture;
    }
}
