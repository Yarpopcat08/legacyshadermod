package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.ShadersTess;
import me.andreasmelone.legacyshadermod.mixinif.IShaderTessellator;
import net.minecraft.src.Tessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Tessellator.class)
public abstract class TessellatorMixin implements IShaderTessellator {
    @Shadow
    private boolean hasNormals;

    @Shadow
    public abstract void setTranslation(double x, double y, double z);

    @Unique
    private ShadersTess shadersTess;

    @Override
    public ShadersTess shadermod$getShadersTess() {
        return shadersTess;
    }

    @Override
    public void shadermod$setShadersTess(ShadersTess tess) {
        this.shadersTess = tess;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int var1, CallbackInfo ci) {
        this.shadersTess = new ShadersTess();
    }

    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    private void onEnd(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ShadersTess.draw((Tessellator) (Object) this));
    }

    @Inject(method = "addVertex(DDD)V", at = @At("HEAD"), cancellable = true)
    private void onVertex(double x, double y, double z, CallbackInfo ci) {
        ShadersTess.addVertex((Tessellator) (Object) this, x, y, z);
        ci.cancel();
    }

    @Inject(method = "setNormal", at = @At("HEAD"), cancellable = true)
    private void onNormal(float x, float y, float z, CallbackInfo ci) {
        this.hasNormals = true;
        this.shadersTess.normalX = x;
        this.shadersTess.normalY = y;
        this.shadersTess.normalZ = z;
        ci.cancel();
    }
}
