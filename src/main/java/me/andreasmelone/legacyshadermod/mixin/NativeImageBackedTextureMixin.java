package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.ShadersTex;
import me.andreasmelone.legacyshadermod.mixinif.IShaderTexture;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NativeImageBackedTexture.class)
public abstract class NativeImageBackedTextureMixin extends AbstractTexture implements IShaderTexture {
    @Mutable
    @Final
    @Shadow
    private int[] pixels;
    @Final
    @Shadow
    private int width;
    @Final
    @Shadow
    private int height;

    @Inject(method = "<init>(II)V", at = @At("RETURN"))
    private void onInit(int var1, int var2, CallbackInfo ci) {
        this.pixels = new int[var1 * var2 * 3];
        ShadersTex.initDynamicTexture(this.getGlId(), var1, var2, (NativeImageBackedTexture) (Object) this);
    }

    @Inject(method = "upload", at = @At("HEAD"), cancellable = true)
    private void onUpdate(CallbackInfo ci) {
        ShadersTex.updateDynamicTexture(this.getGlId(), this.pixels, this.width, this.height, (NativeImageBackedTexture) (Object) this);
        ci.cancel();
    }
}
