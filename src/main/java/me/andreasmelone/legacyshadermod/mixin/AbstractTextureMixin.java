package me.andreasmelone.legacyshadermod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.andreasmelone.legacyshadermod.client.MultiTexID;
import me.andreasmelone.legacyshadermod.client.ShadersTex;
import me.andreasmelone.legacyshadermod.mixinif.IShaderTexture;
import net.minecraft.src.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractTexture.class)
public abstract class AbstractTextureMixin implements IShaderTexture {
    @Shadow
    public int glTextureId;
    @Unique
    public MultiTexID multiTex;

    @Override
    public MultiTexID shadermod$getMultiTexID() {
        return this.shadermod$getOriginalMultiTexId();
    }

    @Override
    public MultiTexID shadermod$getOriginalMultiTexId() {
        return ShadersTex.getMultiTexID((AbstractTexture) (Object) this);
    }

    @Override
    public MultiTexID shadermod$getInternallyStoredMultiTexId() {
        return multiTex;
    }

    @Override
    public void shadermod$setMultiTexID(MultiTexID id) {
        this.multiTex = id;
    }
}
