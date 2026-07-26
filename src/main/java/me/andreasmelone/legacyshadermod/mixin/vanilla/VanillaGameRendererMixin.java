package me.andreasmelone.legacyshadermod.mixin.vanilla;

import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.src.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class VanillaGameRendererMixin {
    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderGlobal;sortAndRender(Lnet/minecraft/src/EntityLivingBase;ID)I",
                    ordinal = 1
            )
    )
    public void beginFancyWater(float limitTime, long par2, CallbackInfo ci) {
        Shaders.beginWaterFancy();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderGlobal;sortAndRender(Lnet/minecraft/src/EntityLivingBase;ID)I",
                    ordinal = 2
            )
    )
    public void beginNormalWater(float limitTime, long par2, CallbackInfo ci) {
        Shaders.beginWater();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderGlobal;sortAndRender(Lnet/minecraft/src/EntityLivingBase;ID)I",
                    ordinal = 2,
                    shift = At.Shift.AFTER
            )
    )
    public void endWater(float limitTime, long par2, CallbackInfo ci) {
        Shaders.endWater();
    }
}
