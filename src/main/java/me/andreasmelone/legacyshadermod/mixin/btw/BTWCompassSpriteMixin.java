package me.andreasmelone.legacyshadermod.mixin.btw;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import net.minecraft.src.TextureCompass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextureCompass.class)
public class BTWCompassSpriteMixin {
    @WrapOperation(
            method = "updateCompass(Lnet/minecraft/src/World;DDDZZLnet/minecraft/src/EntityPlayer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/TextureUtil;uploadTextureSub([IIIIIZZ)V"
            )
    )
    private void redirectUpdateSubImage(int[] is, int i, int j, int k, int l, boolean bl, boolean bl2, Operation<Void> original) {
        ShadersTex.updateSubImage(is, i, j, k, l, bl, bl2);
    }

    @WrapOperation(
            method = "updateInert(Lapi/client/CustomUpdatingTexture$CustomUpdateRenderLocation;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/TextureUtil;uploadTextureSub([IIIIIZZ)V"
            )
    )
    private void redirectUpdateInertSubImageMcpatcher(int[] pixels, int width, int height, int x, int y, boolean a, boolean b, Operation<Void> original) {
        ShadersTex.updateSubImage(pixels, width, height, x, y, a, b);
    }

    @WrapOperation(
            method = "updateCompass(Lnet/minecraft/src/World;DDDZZLnet/minecraft/src/EntityPlayer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/TextureUtil;uploadTextureSub([IIIIIZZ)V"
            )
    )
    private void redirectUpdateActiveSubImageMcpatcher(int[] pixels, int width, int height, int x, int y, boolean a, boolean b, Operation<Void> original) {
        ShadersTex.updateSubImage(pixels, width, height, x, y, a, b);
    }
}
