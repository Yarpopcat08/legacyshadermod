package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.ShadersTex;
import me.andreasmelone.legacyshadermod.mixinif.IShaderTexture;
import net.minecraft.src.AbstractTexture;
import net.minecraft.src.DynamicTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DynamicTexture.class)
public abstract class NativeImageBackedTextureMixin extends AbstractTexture implements IShaderTexture {
    @Mutable
    @Final
    @Shadow
    private int[] dynamicTextureData;
    @Final
    @Shadow
    private int width;
    @Final
    @Shadow
    private int height;

    @Inject(method = "<init>(II)V", at = @At("RETURN"))
    private void onInit(int var1, int var2, CallbackInfo ci) {
        this.dynamicTextureData = new int[var1 * var2 * 3];
        ShadersTex.initDynamicTexture(this.getGlTextureId(), var1, var2, (DynamicTexture) (Object) this);
    }

    @Inject(method = "updateDynamicTexture", at = @At("HEAD"), cancellable = true)
    private void onUpdate(CallbackInfo ci) {
        ShadersTex.updateDynamicTexture(this.getGlTextureId(), this.dynamicTextureData, this.width, this.height, (DynamicTexture) (Object) this);
        ci.cancel();
    }
}
