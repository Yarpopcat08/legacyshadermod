package me.andreasmelone.legacyshadermod.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import net.minecraft.src.TextureCompass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextureCompass.class)
public class VanillaCompassSpriteMixin {
    @WrapOperation(
            method = "method_5241(Lnet/minecraft/class_1150;DDDZZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/TextureUtil;uploadTextureSub([IIIIIZZ)V"
            )
    )
    private void redirectUpdateSubImage(int[] is, int i, int j, int k, int l, boolean bl, boolean bl2, Operation<Void> original) {
        ShadersTex.updateSubImage(is, i, j, k, l, bl, bl2);
    }
}
