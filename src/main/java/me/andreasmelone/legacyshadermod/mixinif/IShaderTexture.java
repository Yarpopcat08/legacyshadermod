package me.andreasmelone.legacyshadermod.mixinif;

import me.andreasmelone.legacyshadermod.client.MultiTexID;

public interface IShaderTexture {
    default MultiTexID shadermod$getMultiTexID() {
        return shadermod$getOriginalMultiTexId();
    }

    void shadermod$setMultiTexID(MultiTexID id);

    MultiTexID shadermod$getOriginalMultiTexId();

    MultiTexID shadermod$getInternallyStoredMultiTexId();
}
