package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.src.EntityEnderman;
import net.minecraft.src.RenderEnderman;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderEnderman.class)
public class EndermanEntityRendererMixin {
    @Inject(method = "renderEyes", at = @At("RETURN"))
    private void onBeforeEyes(EntityEnderman i, int f, float par3, CallbackInfoReturnable<Integer> cir) {
        Shaders.beginSpiderEyes();
    }
}
