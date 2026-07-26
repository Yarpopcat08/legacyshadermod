package me.andreasmelone.legacyshadermod.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import net.minecraft.src.TextureClock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextureClock.class)
public class VanillaClockSpriteMixin {
    @WrapOperation(
            method = "updateAnimation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/TextureUtil;uploadTextureSub([IIIIIZZ)V"
            )
    )
    private void redirectUpdateSubImage(int[] pixels, int width, int height, int x, int y, boolean a, boolean b, Operation<Void> original) {
        ShadersTex.updateSubImage(pixels, width, height, x, y, a, b);
    }
}
