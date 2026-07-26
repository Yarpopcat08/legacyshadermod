package me.andreasmelone.legacyshadermod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.RenderGlobal;
import net.minecraft.src.Tessellator;
import net.minecraft.src.Vec3;
import net.minecraft.src.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderGlobal.class)
public class WorldRendererMixin {
    // ============== method 1370 start ==================

    @Inject(
            method = "renderEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/Profiler;endStartSection(Ljava/lang/String;)V",
                    shift = At.Shift.AFTER,
                    ordinal = 1
            )
    )
    private void onBeforeEntities(CallbackInfo ci) {
        Shaders.beginEntities();
    }

    @Inject(
            method = "renderEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/Profiler;endStartSection(Ljava/lang/String;)V",
                    shift = At.Shift.AFTER,
                    ordinal = 2
            )
    )
    private void onAfterEntitiesBeforeTileEntities(CallbackInfo ci) {
        Shaders.endEntities();
        Shaders.beginTileEntities();
    }

    @Inject(
            method = "renderEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/EntityRenderer;disableLightmap(D)V",
                    shift = At.Shift.AFTER
            )
    )
    private void onAfterTileEntities(CallbackInfo ci) {
        Shaders.endTileEntities();
    }

    // ============== method 1370 end ==================

    // ============== method 1374 start ==================

    @Inject(
            method = "sortAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
                    ordinal = 1
            )
    )
    private void inject1374_1(EntityLivingBase i, int d, double par3, CallbackInfoReturnable<Integer> cir) {
        Shaders.disableTexture2D();
    }

    @Inject(
            method = "sortAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColorMask(ZZZZ)V",
                    ordinal = 0
            )
    )
    private void inject1374_2(EntityLivingBase i, int d, double par3, CallbackInfoReturnable<Integer> cir) {
        Shaders.disableFog();
    }

    @Inject(
            method = "sortAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
                    ordinal = 1
            )
    )
    private void inject1374_3(EntityLivingBase i, int d, double par3, CallbackInfoReturnable<Integer> cir) {
        Shaders.enableTexture2D();
    }

    @Inject(
            method = "sortAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/Profiler;endStartSection(Ljava/lang/String;)V",
                    ordinal = 2
            )
    )
    private void inject1374_4(EntityLivingBase i, int d, double par3, CallbackInfoReturnable<Integer> cir) {
        Shaders.enableFog();
    }

    // ============== method 1374 start ==================

    // ============== method renderSky start ==================

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
                    ordinal = 1
            )
    )
    private void onRenderSky1(float tickDelta, CallbackInfo ci) {
        Shaders.disableFog();
    }

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
                    ordinal = 2
            )
    )
    private void onRenderSky2(float tickDelta, CallbackInfo ci) {
        Shaders.enableTexture2D();
    }

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/WorldClient;getSkyColor(Lnet/minecraft/src/Entity;F)Lnet/minecraft/src/Vec3;"
            )
    )
    public void onRenderSky3(float par1, CallbackInfo ci) {
        Shaders.disableTexture2D();
    }

    @WrapOperation(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/WorldClient;getSkyColor(Lnet/minecraft/src/Entity;F)Lnet/minecraft/src/Vec3;"
            )
    )
    public Vec3 onRenderSky4(WorldClient instance, Entity entity, float v, Operation<Vec3> original) {
        Vec3 orig = original.call(instance, entity, v);
        Shaders.setSkyColor(orig);
        return orig;
    }

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor3f(FFF)V",
                    ordinal = 1
            )
    )
    public void onRenderSky5(float par1, CallbackInfo ci) {
        Shaders.enableFog();
    }

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor3f(FFF)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    public void onRenderSky6(float par1, CallbackInfo ci) {
        Shaders.preSkyList();
    }

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
                    ordinal = 4
            )
    )
    public void onRenderSky7(float par1, CallbackInfo ci) {
        Shaders.disableFog();
    }

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glShadeModel(I)V",
                    ordinal = 0
            )
    )
    public void onRenderSky8(float par1, CallbackInfo ci) {
        Shaders.disableTexture2D();
    }

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glBlendFunc(II)V",
                    ordinal = 2
            )
    )
    public void onRenderSky9(float par1, CallbackInfo ci) {
        Shaders.enableTexture2D();
    }

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V",
                    ordinal = 9
            )
    )
    public void onRenderSky10(float par1, CallbackInfo ci) {
        Shaders.preCelestialRotate();
    }

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V",
                    ordinal = 9,
                    shift = At.Shift.AFTER
            )
    )
    public void onRenderSky11(float par1, CallbackInfo ci) {
        Shaders.postCelestialRotate();
    }

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/WorldClient;getStarBrightness(F)F",
                    ordinal = 0
            )
    )
    public void onRenderSky12(float par1, CallbackInfo ci) {
        Shaders.disableTexture2D();
    }

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V",
                    ordinal = 2
            )
    )
    public void onRenderSky13(float par1, CallbackInfo ci) {
        Shaders.enableFog();
    }

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor3f(FFF)V",
                    ordinal = 2
            )
    )
    public void onRenderSky14(float par1, CallbackInfo ci) {
        Shaders.disableTexture2D();
    }

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glDepthMask(Z)V",
                    ordinal = 3
            )
    )
    public void onRenderSky16(float par1, CallbackInfo ci) {
        Shaders.enableTexture2D();
    }

    // ============== method 1374 end ==================

    // ============== method 1372 start ==================

    @Inject(
            method = "drawBlockDamageTexture",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V",
                    ordinal = 0
            )
    )
    private void inject1372_1(Tessellator playerEntity, EntityPlayer f, float par3, CallbackInfo ci) {
        Shaders.beginBlockDestroyProgress();
    }

    @Inject(
            method = "drawBlockDamageTexture",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void inject1372_tail(Tessellator playerEntity, EntityPlayer f, float par3, CallbackInfo ci) {
        Shaders.endBlockDestroyProgress();
    }

    // ============== method 1372 end ==================

    // ============== method drawBlockOutline start ==================

    @Inject(
            method = "drawSelectionBox",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glDepthMask(Z)V",
                    ordinal = 0
            )
    )
    public void onDrawBlockOutline1(EntityPlayer hitResult, MovingObjectPosition i, int tickDelta, float par4, CallbackInfo ci) {
        Shaders.disableTexture2D();
    }

    @Inject(
            method = "drawSelectionBox",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
                    ordinal = 1
            )
    )
    public void onDrawBlockOutline2(EntityPlayer hitResult, MovingObjectPosition i, int tickDelta, float par4, CallbackInfo ci) {
        Shaders.enableTexture2D();
    }

    // ============== method drawBlockOutline end ==================
}
