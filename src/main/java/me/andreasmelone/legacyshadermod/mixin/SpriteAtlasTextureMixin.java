package me.andreasmelone.legacyshadermod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import me.andreasmelone.legacyshadermod.mixinif.IShaderTexture;
import me.andreasmelone.legacyshadermod.mixinif.ISpriteAtlasTexture;
import net.minecraft.client.render.TextureStitcher;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(SpriteAtlasTexture.class)
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

    @WrapOperation(method = "method_5827", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Lnet/minecraft/resource/Resource;"))
    private Resource redirectGetResource(ResourceManager instance, Identifier identifier, Operation<Resource> original) throws IOException {
        return ShadersTex.loadResource(instance, identifier);
    }

    @WrapOperation(
            method = "method_5827",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/TextureUtil;prepareImage(III)V"
            ),
            require = 0,
            expect = 0,
            allow = 1
    )
    private void replaceSetupTextureMap(int id, int width, int height, Operation<Void> original, @Local TextureStitcher stitcher) {
        ShadersTex.setupTextureMap(
                ((SpriteAtlasTexture) (Object) this).getGlId(), width, height, stitcher, (SpriteAtlasTexture) (Object) this
        );
    }

    @SuppressWarnings({"InvalidInjectorMethodSignature", "MixinAnnotationTarget"})
    @WrapOperation(
            method = "method_5827",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/prupe/mcpatcher/hd/MipmapHelper;setupTexture(ILjava/awt/image/BufferedImage;ZZLnet/minecraft/util/Identifier;)I"
            ),
            require = 0,
            expect = 0,
            allow = 1
    )
    private void replaceSetupTextureMapMcpatcher(int id, int width, int height, Operation<Void> original, Identifier textureName, @Local TextureStitcher stitcher) {
        ShadersTex.setupTextureMap(
                ((SpriteAtlasTexture) (Object) this).getGlId(), width, height, stitcher, (SpriteAtlasTexture) (Object) this
        );
    }

    // vanilla
    @WrapOperation(
            method = "method_5827",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/TextureUtil;method_5868([IIIIIZZ)V"
            ),
            require = 0,
            expect = 0,
            allow = 1
    )
    private void redirectUpdateTextureMap(int[] is, int i, int j, int k, int l, boolean bl, boolean bl2, Operation<Void> original) {
        ShadersTex.updateTextureMap(is, i, j, k, l, bl, bl2);
    }

    // mcpatcher
    @SuppressWarnings({"InvalidInjectorMethodSignature", "MixinAnnotationTarget"})
    @WrapOperation(
            method = "method_5827",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/prupe/mcpatcher/hd/MipmapHelper;copySubTexture([IIIIILjava/lang/String;)V"
            ),
            require = 0,
            expect = 0,
            allow = 1
    )
    private void redirectUpdateTextureMapMcpatcher(int[] rgb, int width, int height, int x, int y, String textureName, Operation<Void> original) {
        ShadersTex.updateTextureMap(rgb, width, height, x, y, false, false);
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void onUpdateHead(CallbackInfo ci) {
        ShadersTex.updatingTex = this.shadermod$getMultiTexID();
    }

    @Inject(method = "update", at = @At("RETURN"))
    private void onUpdateReturn(CallbackInfo ci) {
        ShadersTex.updatingTex = null;
    }
}
