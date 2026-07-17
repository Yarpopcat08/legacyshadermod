package me.andreasmelone.legacyshadermod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import me.andreasmelone.legacyshadermod.mixinif.IShaderTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.image.BufferedImage;

@Mixin(ResourceTexture.class)
public abstract class ResourceTextureMixin implements IShaderTexture {
    @Final
    @Shadow
    private Identifier field_6555;

    @Inject(
            method = "load",
            at = @At("HEAD")
    )
    private void loadHead(ResourceManager par1, CallbackInfo ci, @Share("manager") LocalRef<ResourceManager> managerRef) {
        managerRef.set(par1);
    }

    // vanilla
    @WrapOperation(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/TextureUtil;method_5860(ILjava/awt/image/BufferedImage;ZZ)I"
            ),
            require = 0,
            expect = 0,
            allow = 1
    )
    private int wrapLoadTexture(int id, BufferedImage image, boolean linear, boolean clamp, Operation<Integer> original, @Share("manager") LocalRef<ResourceManager> managerRef) {
        return ShadersTex.loadSimpleTexture(id, image, linear, clamp, managerRef.get(), field_6555, this.shadermod$getMultiTexID());
    }

    // mcpatcher
    @SuppressWarnings({"InvalidInjectorMethodSignature", "MixinAnnotationTarget"})
    @WrapOperation(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/prupe/mcpatcher/hd/MipmapHelper;setupTexture(ILjava/awt/image/BufferedImage;ZZLnet/minecraft/util/Identifier;)I"
            ),
            require = 0,
            expect = 0,
            allow = 1
    )
    private int wrapLoadTextureMcpatcher(int glTexture, BufferedImage image, boolean blur, boolean clamp, Identifier textureName, Operation<Integer> original, @Share("manager") LocalRef<ResourceManager> managerRef) {
        return ShadersTex.loadSimpleTexture(glTexture, image, blur, clamp, managerRef.get(), field_6555, this.shadermod$getMultiTexID());
    }
}
