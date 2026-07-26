package me.andreasmelone.legacyshadermod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import net.minecraft.src.AnimationMetadataSection;
import net.minecraft.src.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.image.BufferedImage;
import java.util.List;

@Mixin(TextureAtlasSprite.class)
public abstract class SpriteMixin {
    @Shadow
    protected int width;
    @Shadow
    protected int height;
    @Shadow
    protected List framesTextureData;

    @Shadow
    protected int originY;

    @Shadow
    protected int originX;

    @WrapOperation(
            method = "loadSprite",
            at = @At(value = "INVOKE", target = "Ljava/awt/image/BufferedImage;getWidth()I")
    )
    public int wrapop5834_1(BufferedImage instance, Operation<Integer> original, @Share("origWidth") LocalIntRef origWidthShare) {
        int origWidth = original.call(instance);
        origWidthShare.set(origWidth);
        return origWidth * 3;
    }

    @WrapOperation(
            method = "loadSprite",
            at = @At(value = "INVOKE", target = "Ljava/awt/image/BufferedImage;getRGB(IIII[III)[I")
    )
    public int[] wrapop5834_2(BufferedImage instance, int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize, Operation<int[]> original, @Share("origWidth") LocalIntRef origWidthShare) {
        this.width = origWidthShare.get();

        ShadersTex.loadAtlasSprite(instance, 0, 0, this.width, this.height, rgbArray, 0, this.width);

        return rgbArray;
    }

    @WrapOperation(method = "loadSprite", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureAtlasSprite;getFrameTextureData([IIII)[I", ordinal = 0))
    private int[] redirectExtractFrame1(int[] is, int i, int j, int k, Operation<int[]> original) {
        return ShadersTex.extractFrame(is, i, j, k);
    }

    @WrapOperation(method = "loadSprite", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureAtlasSprite;getFrameTextureData([IIII)[I", ordinal = 1))
    private int[] redirectExtractFrame2(int[] is, int i, int j, int k, Operation<int[]> original) {
        return ShadersTex.extractFrame(is, i, j, k);
    }
}
