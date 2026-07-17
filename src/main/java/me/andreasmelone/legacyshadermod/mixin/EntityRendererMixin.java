package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private void onRenderShadowHead(Entity entity, double x, double y, double z, float shadowSize, float tickDelta, CallbackInfo ci) {
        if (Shaders.shouldSkipDefaultShadow) {
            ci.cancel();
        }
    }
}
