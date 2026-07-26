package me.andreasmelone.legacyshadermod.mixin.mcpatcher;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import net.minecraft.src.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(TextureAtlasSprite.class)
public class MCPatcherSpriteMixin {
    @Shadow
    public List framesTextureData;

    @Shadow
    protected int width;

    @Shadow
    protected int height;

    @Shadow
    protected int originX;

    @Shadow
    protected int originY;

    @WrapOperation(
            method = "updateAnimation",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/prupe/mcpatcher/hd/MipmapHelper;copySubTexture(Lnet/minecraft/src/TextureAtlasSprite;I)V"
            )
    )
    private void redirectUpdateSubImage(TextureAtlasSprite texture, int index, Operation<Void> original) {
        ShadersTex.updateSubImage((int[]) this.framesTextureData.get(index), width, height, this.originX, this.originY, false, false);
    }
}
