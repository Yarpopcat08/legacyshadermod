package me.andreasmelone.legacyshadermod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import net.minecraft.client.texture.ClockSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClockSprite.class)
public class ClockSpriteMixin {
    // vanilla
    @WrapOperation(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/TextureUtil;method_5868([IIIIIZZ)V"
            ),
            require = 0,
            expect = 0,
            allow = 1
    )
    private void redirectUpdateSubImage(int[] pixels, int width, int height, int x, int y, boolean a, boolean b, Operation<Void> original) {
        ShadersTex.updateSubImage(pixels, width, height, x, y, a, b);
    }

    // better than wolves
    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    @WrapOperation(
            method = "updateInert(Lapi/client/CustomUpdatingTexture$CustomUpdateRenderLocation;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/TextureUtil;method_5868([IIIIIZZ)V"
            ),
            require = 0,
            expect = 0,
            allow = 1
    )
    private void redirectUpdateInertSubImageMcpatcher(int[] pixels, int width, int height, int x, int y, boolean a, boolean b, Operation<Void> original) {
        ShadersTex.updateSubImage(pixels, width, height, x, y, a, b);
    }

    @SuppressWarnings({"MixinAnnotationTarget", "MixinAnnotationTarget"})
    @WrapOperation(
            method = "updateActive(Lapi/client/CustomUpdatingTexture$CustomUpdateRenderLocation;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/TextureUtil;method_5868([IIIIIZZ)V"
            ),
            require = 0,
            expect = 0,
            allow = 1
    )
    private void redirectUpdateActiveSubImageMcpatcher(int[] pixels, int width, int height, int x, int y, boolean a, boolean b, Operation<Void> original) {
        ShadersTex.updateSubImage(pixels, width, height, x, y, a, b);
    }
}
