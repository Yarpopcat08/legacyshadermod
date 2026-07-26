package me.andreasmelone.legacyshadermod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import me.andreasmelone.legacyshadermod.mixinif.IShaderTexture;
import me.andreasmelone.legacyshadermod.mixinif.ISpriteAtlasTexture;
import net.minecraft.src.Resource;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.ResourceManager;
import net.minecraft.src.Stitcher;
import net.minecraft.src.TextureMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(TextureMap.class)
public abstract class SpriteAtlasTextureMixin implements ISpriteAtlasTexture, IShaderTexture {
    @Unique
    public int atlasWidth;

    @Unique
    public int atlasHeight;

    @Override
    public int shadermod$getAtlasWidth() {
        return atlasWidth;
    }

    @Override
    public void shadermod$setAtlasWidth(int width) {
        this.atlasWidth = width;
    }

    @Override
    public int shadermod$getAtlasHeight() {
        return atlasHeight;
    }

    @Override
    public void shadermod$setAtlasHeight(int height) {
        this.atlasHeight = height;
    }

    @WrapOperation(method = "loadTextureAtlas", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ResourceManager;getResource(Lnet/minecraft/src/ResourceLocation;)Lnet/minecraft/src/Resource;"))
    private Resource redirectGetResource(ResourceManager instance, ResourceLocation identifier, Operation<Resource> original) throws IOException {
        return ShadersTex.loadResource(instance, identifier);
    }

    @Inject(method = "updateAnimations", at = @At("HEAD"))
    private void onUpdateHead(CallbackInfo ci) {
        ShadersTex.updatingTex = this.shadermod$getMultiTexID();
    }

    @Inject(method = "updateAnimations", at = @At("RETURN"))
    private void onUpdateReturn(CallbackInfo ci) {
        ShadersTex.updatingTex = null;
    }
}
