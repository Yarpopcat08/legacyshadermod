package me.andreasmelone.legacyshadermod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.src.Block;
import net.minecraft.src.RenderBlocks;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(RenderBlocks.class)
public class BlockRendererMixin {
    @Inject(method = "renderBlockByRenderType", at = @At("HEAD"))
    private void renderHead(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        Shaders.pushEntity(block);
    }

    @Inject(method = "renderBlockByRenderType", at = @At("RETURN"))
    private void renderReturn(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        Shaders.popEntity();
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodUD(DDDDDDFD)V",
                    ordinal = 0
            )
    )
    public void wrapRenderPistonHead1(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.8f) * Shaders.blockLightLevel08, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodUD(DDDDDDFD)V",
                    ordinal = 1
            )
    )
    public void wrapRenderPistonHead2(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.8f) * Shaders.blockLightLevel08, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodUD(DDDDDDFD)V",
                    ordinal = 2
            )
    )
    public void wrapRenderPistonHead3(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.6f) * Shaders.blockLightLevel06, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodUD(DDDDDDFD)V",
                    ordinal = 3
            )
    )
    public void wrapRenderPistonHead4(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.6f) * Shaders.blockLightLevel06, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodUD(DDDDDDFD)V",
                    ordinal = 4
            )
    )
    public void wrapRenderPistonHead5(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.8f) * Shaders.blockLightLevel08, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodUD(DDDDDDFD)V",
                    ordinal = 5
            )
    )
    public void wrapRenderPistonHead6(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.8f) * Shaders.blockLightLevel08, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodUD(DDDDDDFD)V",
                    ordinal = 6
            )
    )
    public void wrapRenderPistonHead7(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.6f) * Shaders.blockLightLevel06, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodUD(DDDDDDFD)V",
                    ordinal = 7
            )
    )
    public void wrapRenderPistonHead8(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.6f) * Shaders.blockLightLevel06, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodSN(DDDDDDFD)V",
                    ordinal = 0
            )
    )
    public void wrapRenderPistonHead9(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.6f) * Shaders.blockLightLevel06, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodSN(DDDDDDFD)V",
                    ordinal = 1
            )
    )
    public void wrapRenderPistonHead10(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.6f) * Shaders.blockLightLevel06, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodSN(DDDDDDFD)V",
                    ordinal = 2
            )
    )
    public void wrapRenderPistonHead11(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.5f) * Shaders.blockLightLevel05, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodSN(DDDDDDFD)V",
                    ordinal = 4
            )
    )
    public void wrapRenderPistonHead12(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.6f) * Shaders.blockLightLevel06, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodSN(DDDDDDFD)V",
                    ordinal = 5
            )
    )
    public void wrapRenderPistonHead13(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.6f) * Shaders.blockLightLevel06, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodSN(DDDDDDFD)V",
                    ordinal = 6
            )
    )
    public void wrapRenderPistonHead14(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.5f) * Shaders.blockLightLevel05, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodEW(DDDDDDFD)V",
                    ordinal = 0
            )
    )
    public void wrapRenderPistonHead15(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.5f) * Shaders.blockLightLevel05, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodEW(DDDDDDFD)V",
                    ordinal = 2
            )
    )
    public void wrapRenderPistonHead16(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.5f) * Shaders.blockLightLevel06, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodEW(DDDDDDFD)V",
                    ordinal = 3
            )
    )
    public void wrapRenderPistonHead17(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.5f) * Shaders.blockLightLevel06, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodEW(DDDDDDFD)V",
                    ordinal = 4
            )
    )
    public void wrapRenderPistonHead18(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.5f) * Shaders.blockLightLevel05, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodEW(DDDDDDFD)V",
                    ordinal = 6
            )
    )
    public void wrapRenderPistonHead19(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.5f) * Shaders.blockLightLevel06, v);
    }

    @WrapOperation(
            method = "renderPistonExtension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBlocks;renderPistonRodEW(DDDDDDFD)V",
                    ordinal = 7
            )
    )
    public void wrapRenderPistonHead20(RenderBlocks instance, double x1, double y1, double y0, double z0, double z1, double color, float d, double v, Operation<Void> original) {
        original.call(instance, x1, y1, y0, z0, z1, color, (d / 0.5f) * Shaders.blockLightLevel06, v);
    }

    @ModifyConstant(
            method = "renderBlockBed",
            constant = @Constant(floatValue = 0.5F, ordinal = 0)
    )
    private float bed05(float original) {
        return Shaders.blockLightLevel05;
    }

    @ModifyConstant(
            method = "renderBlockBed",
            constant = @Constant(floatValue = 0.8F, ordinal = 0)
    )
    private float bed08(float original) {
        return Shaders.blockLightLevel08;
    }

    @ModifyConstant(
            method = "renderBlockBed",
            constant = @Constant(floatValue = 0.6F, ordinal = 0)
    )
    private float bed06(float original) {
        return Shaders.blockLightLevel06;
    }

    @ModifyConstant(
            method = "renderBlockFluids",
            constant = @Constant(floatValue = 0.5F, ordinal = 0)
    )
    private float fluid05(float original) {
        return Shaders.blockLightLevel05;
    }

    @ModifyConstant(
            method = "renderBlockFluids",
            constant = @Constant(floatValue = 0.8F, ordinal = 0)
    )
    private float fluid08(float original) {
        return Shaders.blockLightLevel08;
    }

    @ModifyConstant(
            method = "renderBlockFluids",
            constant = @Constant(floatValue = 0.6F, ordinal = 0)
    )
    private float fluid06(float original) {
        return Shaders.blockLightLevel06;
    }

    @ModifyConstant(
            method = "renderBlockSandFalling",
            constant = @Constant(floatValue = 0.5F, ordinal = 0)
    )
    private float method1453_05(float original) {
        return Shaders.blockLightLevel05;
    }

    @ModifyConstant(
            method = "renderBlockSandFalling",
            constant = @Constant(floatValue = 0.8F, ordinal = 0)
    )
    private float method1453_08(float original) {
        return Shaders.blockLightLevel08;
    }

    @ModifyConstant(
            method = "renderBlockSandFalling",
            constant = @Constant(floatValue = 0.6F, ordinal = 0)
    )
    private float method1453_06(float original) {
        return Shaders.blockLightLevel06;
    }

    @ModifyConstant(
            method = "renderBlockCactusImpl",
            constant = @Constant(floatValue = 0.5F, ordinal = 0)
    )
    private float renderCactusInternal_05(float original) {
        return Shaders.blockLightLevel05;
    }

    @ModifyConstant(
            method = "renderBlockCactusImpl",
            constant = @Constant(floatValue = 0.8F, ordinal = 0)
    )
    private float renderCactusInternal_08(float original) {
        return Shaders.blockLightLevel08;
    }

    @ModifyConstant(
            method = "renderBlockCactusImpl",
            constant = @Constant(floatValue = 0.6F, ordinal = 0)
    )
    private float renderCactusInternal_06(float original) {
        return Shaders.blockLightLevel06;
    }

    @ModifyConstant(
            method = "renderBlockDoor",
            constant = @Constant(floatValue = 0.5F, ordinal = 0)
    )
    private float renderDoor_05(float original) {
        return Shaders.blockLightLevel05;
    }

    @ModifyConstant(
            method = "renderBlockDoor",
            constant = @Constant(floatValue = 0.8F, ordinal = 0)
    )
    private float renderDoor_08(float original) {
        return Shaders.blockLightLevel08;
    }

    @ModifyConstant(
            method = "renderBlockDoor",
            constant = @Constant(floatValue = 0.6F, ordinal = 0)
    )
    private float renderDoor_06(float original) {
        return Shaders.blockLightLevel06;
    }

    @ModifyConstant(
            method = "renderStandardBlockWithColorMultiplier",
            constant = @Constant(floatValue = 0.5F, ordinal = 0)
    )
    private float renderCubeNoAO_05(float original) {
        return Shaders.blockLightLevel05;
    }

    @ModifyConstant(
            method = "renderStandardBlockWithColorMultiplier",
            constant = @Constant(floatValue = 0.8F, ordinal = 0)
    )
    private float renderCubeNoAO_08(float original) {
        return Shaders.blockLightLevel08;
    }

    @ModifyConstant(
            method = "renderStandardBlockWithColorMultiplier",
            constant = @Constant(floatValue = 0.6F, ordinal = 0)
    )
    private float renderCubeNoAO_06(float original) {
        return Shaders.blockLightLevel06;
    }

    @ModifyConstant(
            method = "renderStandardBlockWithAmbientOcclusion",
            constant = @Constant(floatValue = 0.5F),
            allow = Integer.MAX_VALUE,
            expect = 0,
            require = 0
    )
    private float cubeAO_05(float original) {
        return Shaders.blockLightLevel05;
    }

    @ModifyConstant(
            method = "renderStandardBlockWithAmbientOcclusion",
            constant = @Constant(floatValue = 0.8F),
            allow = Integer.MAX_VALUE,
            expect = 0,
            require = 0
    )
    private float cubeAO_08(float original) {
        return Shaders.blockLightLevel08;
    }

    @ModifyConstant(
            method = "renderStandardBlockWithAmbientOcclusion",
            constant = @Constant(floatValue = 0.6F),
            allow = Integer.MAX_VALUE,
            expect = 0,
            require = 0
    )
    private float cubeAO_06(float original) {
        return Shaders.blockLightLevel06;
    }

    @ModifyConstant(
            method = "renderStandardBlockWithAmbientOcclusionPartial",
            constant = @Constant(floatValue = 0.5F),
            allow = Integer.MAX_VALUE,
            expect = 0,
            require = 0
    )
    private float method_5168_05(float original) {
        return Shaders.blockLightLevel05;
    }

    @ModifyConstant(
            method = "renderStandardBlockWithAmbientOcclusionPartial",
            constant = @Constant(floatValue = 0.8F),
            allow = Integer.MAX_VALUE,
            expect = 0,
            require = 0
    )
    private float method_5168_08(float original) {
        return Shaders.blockLightLevel08;
    }

    @ModifyConstant(
            method = "renderStandardBlockWithAmbientOcclusionPartial",
            constant = @Constant(floatValue = 0.6F),
            allow = Integer.MAX_VALUE,
            expect = 0,
            require = 0
    )
    private float method_5168_06(float original) {
        return Shaders.blockLightLevel06;
    }
}
