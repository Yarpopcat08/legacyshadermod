package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.src.EntitySpider;
import net.minecraft.src.RenderSpider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderSpider.class)
public class SpiderEntityRendererMixin {
    @Inject(method = "setSpiderEyeBrightness", at = @At("TAIL"))
    private void beginEyes(EntitySpider i, int f, float par3, CallbackInfoReturnable<Integer> cir) {
        Shaders.beginSpiderEyes();
    }
}
