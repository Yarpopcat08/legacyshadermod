package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.entity.mob.SpiderEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpiderEntityRenderer.class)
public class SpiderEntityRendererMixin {
    @Inject(method = "method_1598", at = @At("TAIL"))
    private void beginEyes(SpiderEntity i, int f, float par3, CallbackInfoReturnable<Integer> cir) {
        Shaders.beginSpiderEyes();
    }
}
