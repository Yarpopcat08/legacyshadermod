package me.andreasmelone.legacyshadermod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import me.andreasmelone.legacyshadermod.client.Shaders;
import me.andreasmelone.legacyshadermod.client.ShadersRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.CameraView;
import net.minecraft.client.render.CullingCameraView;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.FloatBuffer;

@Debug(export = true)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    private MinecraftClient client;

    @Shadow
    private float viewDistance;

    @Shadow
    protected abstract float getFov(float tickDelta, boolean changingFov);

    @Shadow
    protected abstract void bobViewWhenHurt(float tickDelta);

    @Shadow
    protected abstract void bobView(float tickDelta);

    @Shadow
    public HeldItemRenderer firstPersonRenderer;

    @Shadow
    public abstract void afterWorldRender(double tickDelta);

    @Shadow
    public abstract void beforeWorldRender(double tickDelta);

    @Shadow
    protected abstract void renderHand(float tickDelta, int anaglyphOffset);

    @Inject(
            method = "renderHand",
            at = @At("HEAD")
    )
    private void renderHandHead(float tickDelta, int anaglyphOffset, CallbackInfo ci, @Share("tickdelta") LocalFloatRef tickDeltaRef) {
        tickDeltaRef.set(tickDelta);
    }

    @WrapOperation(
            method = "renderHand",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V")
    )
    private void wrapGluPerspective(float fovy, float aspect, float zNear, float zFar, Operation<Void> original, @Share("tickdelta") LocalFloatRef tickDeltaRef) {
        float var10000 = this.getFov(tickDeltaRef.get(), false);
        float var10001 = (float) this.client.width / this.client.height;
        float var10003 = this.viewDistance * 2.0F;
        Shaders.applyHandDepth();
        original.call(var10000, var10001, 0.05F, var10003);
    }

    @Inject(
            method = "renderHand",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V"
            ),
            cancellable = true
    )
    private void renderHand1(float anaglyphOffset, int par2, CallbackInfo ci) {
        if (!Shaders.isCompositeRendered) {
            this.bobViewWhenHurt(anaglyphOffset);
            if (this.client.options.bobView) {
                this.bobView(anaglyphOffset);
            }

            if (this.client.options.perspective == 0
                    && !this.client.field_6279.isSleeping()
                    && !this.client.options.hudHidden
                    && !this.client.interactionManager.isSpectator()) {
                this.beforeWorldRender(anaglyphOffset);
                this.firstPersonRenderer.renderArmHoldingItem(anaglyphOffset);
                this.afterWorldRender(anaglyphOffset);
            }
        } else {
            if (this.client.options.perspective == 0 && !this.client.field_6279.isSleeping()) {
                this.firstPersonRenderer.renderOverlays(anaglyphOffset);
                this.bobViewWhenHurt(anaglyphOffset);
            }

            if (this.client.options.bobView) {
                this.bobView(anaglyphOffset);
            }
        }
        ci.cancel();
    }

    @Inject(
            method = "afterWorldRender",
            at = @At("RETURN")
    )
    public void afterWorldRenderReturn(double par1, CallbackInfo ci) {
        Shaders.disableLightmap();
    }

    @Inject(
            method = "beforeWorldRender",
            at = @At("RETURN")
    )
    public void beforeWorldRenderReturn(double par1, CallbackInfo ci) {
        Shaders.enableLightmap();
    }

    @Inject(
            method = "renderWorld",
            at = @At("HEAD")
    )
    public void renderWorldHead(float tickDelta, long limitTime, CallbackInfo ci) {
        Shaders.beginRender(this.client, tickDelta, limitTime);
    }

    @WrapOperation(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glViewport(IIII)V"
            )
    )
    public void renderWorld1(int x, int y, int width, int height, Operation<Void> original) {
        Shaders.setViewport(x, y, width, height);
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/entity/player/PlayerEntity;Z)V",
                    ordinal = 0
            )
    )
    public void renderWorld4_1(float limitTime, long par2, CallbackInfo ci) {
        Shaders.clearRenderBuffer();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/entity/player/PlayerEntity;Z)V",
                    ordinal = 0
            )
    )
    public void renderWorld5(float tickDelta, long limitTime, CallbackInfo ci) {
        Shaders.setCamera(tickDelta);
    }

    @ModifyExpressionValue(
            method = "renderWorld",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/option/GameOptions;renderDistance:I",
                    opcode = Opcodes.GETFIELD
            )
    )
    private int modifyRenderDistance(int original) {
        return Shaders.isShadowPass ? 4 : 0;
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderSky(F)V"
            )
    )
    public void renderWorld2(float limitTime, long par2, CallbackInfo ci) {
        Shaders.beginSky();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderSky(F)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderWorld3(float limitTime, long par2, CallbackInfo ci) {
        Shaders.endSky();
    }
//
//   @WrapOperation(
//           method = "renderWorld",
//           at = @At(
//                   value = "INVOKE",
//                   target = "Lnet/minecraft/client/render/CameraView;setPos(DDD)V",
//                   ordinal = 0
//           )
//   )
//   public void renderWorld4(CameraView instance, double v, double v1, double v2, Operation<Void> original) {
//      ShadersRender.setFrustrumPosition((CullingCameraView) instance, v, v1, v2);
//   }

    @WrapOperation(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;method_1373(Lnet/minecraft/client/render/CameraView;F)V"
            )
    )
    public void renderWorld5(WorldRenderer instance, CameraView f, float v, Operation<Void> original, @Local(name = "var13") int var13, @Share("var13Ref") LocalIntRef var13Ref) {
        ShadersRender.clipRenderersByFrustrum(instance, (CullingCameraView) f, v);
        var13Ref.set(var13);
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
                    ordinal = 7
            )
    )
    public void renderWorld6(float limitTime, long par2, CallbackInfo ci) {
        Shaders.beginUpdateChunks();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 1,
                    target = "Lnet/minecraft/entity/LivingEntity;y:D"
            )
    )
    public void renderWorld8(float limitTime, long par2, CallbackInfo ci, @Share("var13Ref") LocalIntRef var13Ref) {
        if (var13Ref.get() == 0) Shaders.endUpdateChunks();
    }

    @WrapOperation(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;method_1374(Lnet/minecraft/entity/LivingEntity;ID)I",
                    ordinal = 0
            )
    )
    public int renderWorld9(WorldRenderer instance, LivingEntity i, int d, double v, Operation<Integer> original) {
        Shaders.beginTerrain();
        int ret = instance.method_1374(i, d, v);
        Shaders.endTerrain();
        return ret;
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/ParticleManager;method_1299(Lnet/minecraft/entity/Entity;F)V",
                    ordinal = 0
            )
    )
    public void renderWorld10(float limitTime, long par2, CallbackInfo ci) {
        Shaders.beginLitParticles();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lnet/minecraft/entity/Entity;F)V",
                    ordinal = 0
            )
    )
    public void renderWorld11(float limitTime, long par2, CallbackInfo ci) {
        Shaders.beginParticles();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lnet/minecraft/entity/Entity;F)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    public void renderWorld12(float limitTime, long par2, CallbackInfo ci) {
        Shaders.endParticles();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;renderFog(IF)V",
                    ordinal = 4
            )
    )
    public void renderWorld13(float limitTime, long par2, CallbackInfo ci, @Share("var13") LocalIntRef var13Ref) {
        Shaders.beginHand();
        this.renderHand(limitTime, var13Ref.get());
        Shaders.endHand();
        Shaders.preWater();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;method_1374(Lnet/minecraft/entity/LivingEntity;ID)I",
                    ordinal = 1
            )
    )
    public void renderWorld14(float limitTime, long par2, CallbackInfo ci) {
        Shaders.beginWaterFancy();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;method_1366(ID)V",
                    ordinal = 0
            )
    )
    public void renderWorld15(float limitTime, long par2, CallbackInfo ci) {
        Shaders.midWaterFancy();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glShadeModel(I)V",
                    ordinal = 3
            )
    )
    public void renderWorld16(float limitTime, long par2, CallbackInfo ci) {
        Shaders.endWater();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;method_1374(Lnet/minecraft/entity/LivingEntity;ID)I",
                    ordinal = 2
            )
    )
    public void renderWorld17(float limitTime, long par2, CallbackInfo ci) {
        Shaders.beginWater();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;method_1374(Lnet/minecraft/entity/LivingEntity;ID)I",
                    ordinal = 2,
                    shift = At.Shift.AFTER
            )
    )
    public void renderWorld18(float limitTime, long par2, CallbackInfo ci) {
        Shaders.endWater();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
                    ordinal = 3,
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    public void renderWorld19(float limitTime, long par2, CallbackInfo ci) {
        if (Shaders.isShadowPass) {
            ci.cancel();
            return;
        }

        Shaders.readCenterDepth();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;renderWeather(F)V",
                    ordinal = 0
            )
    )
    public void renderWorld20(float limitTime, long par2, CallbackInfo ci) {
        Shaders.beginWeather();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;renderWeather(F)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    public void renderWorld21(float limitTime, long par2, CallbackInfo ci) {
        Shaders.endWeather();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
                    ordinal = 6,
                    shift = At.Shift.AFTER
            )
    )
    public void renderWorld22(float limitTime, long par2, CallbackInfo ci) {
        Shaders.disableFog();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
                    ordinal = 19,
                    shift = At.Shift.AFTER
            )
    )
    public void renderWorld23(float limitTime, long par2, CallbackInfo ci) {
        Shaders.renderCompositeFinal();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;renderHand(FI)V",
                    ordinal = 0
            )
    )
    public void renderWorld24(float limitTime, long par2, CallbackInfo ci) {
        Shaders.beginFPOverlay();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;renderHand(FI)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    public void renderWorld25(float limitTime, long par2, CallbackInfo ci) {
        Shaders.endFPOverlay();
    }

    @ModifyExpressionValue(
            method = "renderWorld",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/option/GameOptions;anaglyph3d:Z",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 2
            )
    )
    public boolean renderWorld26(boolean original) {
        Shaders.endRender();
        return original;
    }

    @WrapOperation(
            method = "method_4300",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/GameOptions;method_876()Z"
            )
    )
    public boolean wrapMethod4300(GameOptions instance, Operation<Boolean> original) {
        return Shaders.shouldRenderClouds(instance);
    }

    @Inject(
            method = "method_4300",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;method_1377(F)V"
            )
    )
    public void inject4300_1(WorldRenderer f, float par2, CallbackInfo ci) {
        Shaders.beginClouds();
    }

    @Inject(
            method = "method_4300",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;method_1377(F)V",
                    shift = At.Shift.AFTER
            )
    )
    public void inject4300_2(WorldRenderer f, float par2, CallbackInfo ci) {
        Shaders.endClouds();
    }

    @WrapOperation(
            method = "renderFog",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glFogi(II)V"
            ),
            require = 0,
            expect = 0,
            allow = Integer.MAX_VALUE
    )
    public void renderFog1(int pname, int param, Operation<Void> original) {
        Shaders.sglFogi(pname, param);
    }

    @Inject(
            method = "updateFogColorBuffer",
            at = @At("HEAD")
    )
    private void updateFogColorBufferHead(float red, float green, float blue, float alpha, CallbackInfoReturnable<FloatBuffer> cir) {
        Shaders.setFogColor(red, green, blue);
    }
}
