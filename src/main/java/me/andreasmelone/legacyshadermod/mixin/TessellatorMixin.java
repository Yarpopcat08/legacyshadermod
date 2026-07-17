package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.ShadersTess;
import me.andreasmelone.legacyshadermod.mixinif.IShaderTessellator;
import net.minecraft.client.render.Tessellator;
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
    private boolean hasNormal;

    @Shadow
    public abstract void offset(double x, double y, double z);

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

    @Inject(method = "end", at = @At("HEAD"), cancellable = true)
    private void onEnd(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ShadersTess.draw((Tessellator) (Object) this));
    }

    @Inject(method = "vertex(DDD)V", at = @At("HEAD"), cancellable = true)
    private void onVertex(double x, double y, double z, CallbackInfo ci) {
        ShadersTess.addVertex((Tessellator) (Object) this, x, y, z);
        ci.cancel();
    }

    @Inject(method = "normal", at = @At("HEAD"), cancellable = true)
    private void onNormal(float x, float y, float z, CallbackInfo ci) {
        this.hasNormal = true;
        this.shadersTess.normalX = x;
        this.shadersTess.normalY = y;
        this.shadersTess.normalZ = z;
        ci.cancel();
    }
}
