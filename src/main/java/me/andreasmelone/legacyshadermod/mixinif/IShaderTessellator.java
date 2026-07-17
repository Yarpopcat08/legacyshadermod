package me.andreasmelone.legacyshadermod.mixinif;

import me.andreasmelone.legacyshadermod.client.ShadersTess;

public interface IShaderTessellator {
    ShadersTess shadermod$getShadersTess();

    void shadermod$setShadersTess(ShadersTess tess);
}
