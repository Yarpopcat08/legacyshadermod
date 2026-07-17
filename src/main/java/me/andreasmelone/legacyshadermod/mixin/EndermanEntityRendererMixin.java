package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.client.render.entity.EndermanEntityRenderer;
import net.minecraft.entity.mob.EndermanEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntityRenderer.class)
public class EndermanEntityRendererMixin {
    @Inject(method = "method_1515", at = @At("RETURN"))
    private void onBeforeEyes(EndermanEntity i, int f, float par3, CallbackInfoReturnable<Integer> cir) {
        Shaders.beginSpiderEyes();
    }
}
