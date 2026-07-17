package me.andreasmelone.legacyshadermod.mixinif;

public interface IModelPart {
    boolean shadermod$getCompiled();

    int shadermod$getDisplayList();

    void shadermod$resetDisplayList();
}
