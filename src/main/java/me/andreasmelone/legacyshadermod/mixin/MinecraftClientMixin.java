package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(
            method = "initializeGame",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;<init>(Lnet/minecraft/client/MinecraftClient;)V"
            )
    )
    private void onInit(CallbackInfo ci) {
        Shaders.startup((MinecraftClient) (Object) this);
    }

//   @WrapOperation(
//           method = "initializeGame",
//           at = @At(
//                   value = "INVOKE",
//                   target = "Lorg/lwjgl/opengl/Display;create(Lorg/lwjgl/opengl/PixelFormat;)V"
//           )
//   )
//   private void displayWrap(PixelFormat pixel_format, Operation<Void> original) throws LWJGLException {
//      Display.create(pixel_format, new ContextAttribs(3, 2, ContextAttribs.CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB));
//   }
}
