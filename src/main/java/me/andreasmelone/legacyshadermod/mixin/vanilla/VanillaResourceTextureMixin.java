package me.andreasmelone.legacyshadermod.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import me.andreasmelone.legacyshadermod.mixinif.IShaderTexture;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.ResourceManager;
import net.minecraft.src.SimpleTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.image.BufferedImage;

@Mixin(SimpleTexture.class)
public abstract class VanillaResourceTextureMixin implements IShaderTexture {
    @Shadow
    @Final
    private ResourceLocation textureLocation;

    @Inject(
            method = "loadTexture",
            at = @At("HEAD")
    )
    private void loadHead(ResourceManager par1, CallbackInfo ci, @Share("manager") LocalRef<ResourceManager> managerRef) {
        managerRef.set(par1);
    }

    @WrapOperation(
            method = "loadTexture",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/TextureUtil;uploadTextureImageAllocate(ILjava/awt/image/BufferedImage;ZZ)I"
            )
    )
    private int wrapLoadTexture(int id, BufferedImage image, boolean linear, boolean clamp, Operation<Integer> original, @Share("manager") LocalRef<ResourceManager> managerRef) {
        return ShadersTex.loadSimpleTexture(id, image, linear, clamp, managerRef.get(), textureLocation, this.shadermod$getMultiTexID());
    }
}
