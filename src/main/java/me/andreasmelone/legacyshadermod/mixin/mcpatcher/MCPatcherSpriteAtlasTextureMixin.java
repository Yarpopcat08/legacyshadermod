package me.andreasmelone.legacyshadermod.mixin.mcpatcher;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import net.minecraft.src.Stitcher;
import net.minecraft.src.TextureMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextureMap.class)
public class MCPatcherSpriteAtlasTextureMixin {
    @WrapOperation(
            method = "loadTextureAtlas",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/prupe/mcpatcher/hd/MipmapHelper;setupTexture(IIILjava/lang/String;)V"
            )
    )
    private void replaceSetupTextureMapMcpatcher(int id, int width, int height, String basePath, Operation<Void> original, @Local Stitcher stitcher) {
        ShadersTex.setupTextureMap(
                id, width, height, stitcher, (TextureMap) (Object) this
        );
    }

    @WrapOperation(
            method = "loadTextureAtlas",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/prupe/mcpatcher/hd/MipmapHelper;copySubTexture([IIIIILjava/lang/String;)V"
            )
    )
    private void redirectUpdateTextureMapMcpatcher(int[] rgb, int width, int height, int x, int y, String textureName, Operation<Void> original) {
        ShadersTex.updateTextureMap(rgb, width, height, x, y, false, false);
    }
}
