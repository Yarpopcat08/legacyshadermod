package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.GLXActiveTexture;
import net.minecraft.src.OpenGlHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenGlHelper.class)
public class GLXMixin {
    @Inject(
            method = "setActiveTexture",
            at = @At("HEAD")
    )
    private static void gl13ActiveTexture(int texture, CallbackInfo ci) {
        GLXActiveTexture.activeTexUnit = texture;
    }
}
