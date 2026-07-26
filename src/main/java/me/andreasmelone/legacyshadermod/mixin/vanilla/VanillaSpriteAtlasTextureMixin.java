package me.andreasmelone.legacyshadermod.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import net.minecraft.src.Stitcher;
import net.minecraft.src.TextureMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextureMap.class)
public class VanillaSpriteAtlasTextureMixin {
    @WrapOperation(
            method = "loadTextureAtlas",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/TextureUtil;allocateTexture(III)V"
            )
    )
    private void replaceSetupTextureMap(int id, int width, int height, Operation<Void> original, @Local Stitcher stitcher) {
        ShadersTex.setupTextureMap(
                ((TextureMap) (Object) this).getGlTextureId(), width, height, stitcher, (TextureMap) (Object) this
        );
    }

    @WrapOperation(
            method = "loadTextureAtlas",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/TextureUtil;uploadTextureSub([IIIIIZZ)V"
            )
    )
    private void redirectUpdateTextureMap(int[] is, int i, int j, int k, int l, boolean bl, boolean bl2, Operation<Void> original) {
        ShadersTex.updateTextureMap(is, i, j, k, l, bl, bl2);
    }
}
