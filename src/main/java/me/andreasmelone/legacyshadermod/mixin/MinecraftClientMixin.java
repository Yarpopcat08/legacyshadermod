package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Inject(
            method = "startGame",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderGlobal;<init>(Lnet/minecraft/src/Minecraft;)V"
            )
    )
    private void onInit(CallbackInfo ci) {
        Shaders.startup((Minecraft) (Object) this);
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
