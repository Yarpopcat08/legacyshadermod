package me.andreasmelone.legacyshadermod.mixin.btw;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import net.minecraft.src.TextureClock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextureClock.class)
public class BTWClockSpriteMixin {
    @WrapOperation(
            method = "updateInert(Lapi/client/CustomUpdatingTexture$CustomUpdateRenderLocation;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/TextureUtil;uploadTextureSub([IIIIIZZ)V"
            ),
            require = 0,
            expect = 0,
            allow = 1
    )
    private void redirectUpdateInertSubImageMcpatcher(int[] pixels, int width, int height, int x, int y, boolean a, boolean b, Operation<Void> original) {
        ShadersTex.updateSubImage(pixels, width, height, x, y, a, b);
    }

    @WrapOperation(
            method = "updateActive(Lapi/client/CustomUpdatingTexture$CustomUpdateRenderLocation;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/TextureUtil;uploadTextureSub([IIIIIZZ)V"
            ),
            require = 0,
            expect = 0,
            allow = 1
    )
    private void redirectUpdateActiveSubImageMcpatcher(int[] pixels, int width, int height, int x, int y, boolean a, boolean b, Operation<Void> original) {
        ShadersTex.updateSubImage(pixels, width, height, x, y, a, b);
    }
}
