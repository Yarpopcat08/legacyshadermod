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
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.Frustrum;
import net.minecraft.src.GameSettings;
import net.minecraft.src.ICamera;
import net.minecraft.src.ItemRenderer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.RenderGlobal;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.FloatBuffer;

@Mixin(EntityRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    private Minecraft mc;

    @Shadow
    private float farPlaneDistance;

    @Shadow
    protected abstract float getFOVModifier(float tickDelta, boolean changingFov);

    @Shadow
    protected abstract void hurtCameraEffect(float tickDelta);

    @Shadow
    protected abstract void setupViewBobbing(float tickDelta);

    @Shadow
    public ItemRenderer itemRenderer;

    @Shadow
    public abstract void disableLightmap(double tickDelta);

    @Shadow
    public abstract void enableLightmap(double tickDelta);

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
        float var10000 = this.getFOVModifier(tickDeltaRef.get(), false);
        float var10001 = (float) this.mc.displayWidth / this.mc.displayHeight;
        float var10003 = this.farPlaneDistance * 2.0F;
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
            this.hurtCameraEffect(anaglyphOffset);
            if (this.mc.gameSettings.viewBobbing) {
                this.setupViewBobbing(anaglyphOffset);
            }

            if (this.mc.gameSettings.thirdPersonView == 0
                    && !this.mc.renderViewEntity.isPlayerSleeping()
                    && !this.mc.gameSettings.hideGUI
                    && !this.mc.playerController.enableEverythingIsScrewedUpMode()) {
                this.enableLightmap(anaglyphOffset);
                this.itemRenderer.renderItemInFirstPerson(anaglyphOffset);
                this.disableLightmap(anaglyphOffset);
            }
        } else {
            if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping()) {
                this.itemRenderer.renderOverlays(anaglyphOffset);
                this.hurtCameraEffect(anaglyphOffset);
            }

            if (this.mc.gameSettings.viewBobbing) {
                this.setupViewBobbing(anaglyphOffset);
            }
        }
        ci.cancel();
    }

    @Inject(
            method = "disableLightmap",
            at = @At("RETURN")
    )
    public void afterWorldRenderReturn(double par1, CallbackInfo ci) {
        Shaders.disableLightmap();
    }

    @Inject(
            method = "enableLightmap",
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
        Shaders.beginRender(this.mc, tickDelta, limitTime);
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
                    target = "Lnet/minecraft/src/ActiveRenderInfo;updateRenderInfo(Lnet/minecraft/src/EntityPlayer;Z)V",
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
                    target = "Lnet/minecraft/src/ActiveRenderInfo;updateRenderInfo(Lnet/minecraft/src/EntityPlayer;Z)V",
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
                    target = "Lnet/minecraft/src/GameSettings;renderDistance:I",
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
                    target = "Lnet/minecraft/src/RenderGlobal;renderSky(F)V"
            )
    )
    public void renderWorld2(float limitTime, long par2, CallbackInfo ci) {
        Shaders.beginSky();
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderGlobal;renderSky(F)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderWorld3(float limitTime, long par2, CallbackInfo ci) {
        Shaders.endSky();
    }

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
                    target = "Lnet/minecraft/src/RenderGlobal;clipRenderersByFrustum(Lnet/minecraft/src/ICamera;F)V"
            )
    )
    public void renderWorld5(RenderGlobal instance, ICamera f, float v, Operation<Void> original, @Local(name = "var13") int var13, @Share("var13Ref") LocalIntRef var13Ref) {
        ShadersRender.clipRenderersByFrustrum(instance, (Frustrum) f, v);
        var13Ref.set(var13);
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/Profiler;endStartSection(Ljava/lang/String;)V",
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
                    target = "Lnet/minecraft/src/EntityLivingBase;posY:D"
            )
    )
    public void renderWorld8(float limitTime, long par2, CallbackInfo ci, @Share("var13Ref") LocalIntRef var13Ref) {
        if (var13Ref.get() == 0) Shaders.endUpdateChunks();
    }

    @WrapOperation(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderGlobal;sortAndRender(Lnet/minecraft/src/EntityLivingBase;ID)I",
                    ordinal = 0
            )
    )
    public int renderWorld9(RenderGlobal instance, EntityLivingBase i, int d, double v, Operation<Integer> original) {
        Shaders.beginTerrain();
        int ret = instance.sortAndRender(i, d, v);
        Shaders.endTerrain();
        return ret;
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/EffectRenderer;renderLitParticles(Lnet/minecraft/src/Entity;F)V",
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
                    target = "Lnet/minecraft/src/EffectRenderer;renderParticles(Lnet/minecraft/src/Entity;F)V",
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
                    target = "Lnet/minecraft/src/EffectRenderer;renderParticles(Lnet/minecraft/src/Entity;F)V",
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
                    target = "Lnet/minecraft/src/EntityRenderer;setupFog(IF)V",
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
                    target = "Lnet/minecraft/src/RenderGlobal;renderAllRenderLists(ID)V",
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
                    target = "Lnet/minecraft/src/EntityRenderer;renderRainSnow(F)V",
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
                    target = "Lnet/minecraft/src/EntityRenderer;renderRainSnow(F)V",
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
                    target = "Lnet/minecraft/src/Profiler;endStartSection(Ljava/lang/String;)V",
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
                    target = "Lnet/minecraft/src/EntityRenderer;renderHand(FI)V",
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
                    target = "Lnet/minecraft/src/EntityRenderer;renderHand(FI)V",
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
                    target = "Lnet/minecraft/src/GameSettings;anaglyph:Z",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 2
            )
    )
    public boolean renderWorld26(boolean original) {
        Shaders.endRender();
        return original;
    }

    @WrapOperation(
            method = "renderCloudsCheck",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/GameSettings;shouldRenderClouds()Z"
            )
    )
    public boolean wrapMethod4300(GameSettings instance, Operation<Boolean> original) {
        return Shaders.shouldRenderClouds(instance);
    }

    @Inject(
            method = "renderCloudsCheck",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderGlobal;renderClouds(F)V"
            )
    )
    public void inject4300_1(RenderGlobal f, float par2, CallbackInfo ci) {
        Shaders.beginClouds();
    }

    @Inject(
            method = "renderCloudsCheck",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderGlobal;renderClouds(F)V",
                    shift = At.Shift.AFTER
            )
    )
    public void inject4300_2(RenderGlobal f, float par2, CallbackInfo ci) {
        Shaders.endClouds();
    }

    @WrapOperation(
            method = "setupFog",
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
            method = "setFogColorBuffer",
            at = @At("HEAD")
    )
    private void updateFogColorBufferHead(float red, float green, float blue, float alpha, CallbackInfoReturnable<FloatBuffer> cir) {
        Shaders.setFogColor(red, green, blue);
    }
}
