package me.andreasmelone.legacyshadermod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import net.minecraft.client.texture.CompassSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CompassSprite.class)
public class CompassSpriteMixin {
    // vanilla
    @WrapOperation(
            method = "method_5241",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/TextureUtil;method_5868([IIIIIZZ)V"
            ),
            require = 0,
            expect = 0,
            allow = 1
    )
    private void redirectUpdateSubImage(int[] is, int i, int j, int k, int l, boolean bl, boolean bl2, Operation<Void> original) {
        ShadersTex.updateSubImage(is, i, j, k, l, bl, bl2);
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
            method = "updateCompass(Lnet/minecraft/world/World;DDDZZLnet/minecraft/entity/player/PlayerEntity;)V",
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
