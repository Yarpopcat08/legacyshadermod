package me.andreasmelone.legacyshadermod.client;

import me.andreasmelone.legacyshadermod.mixinif.IShaderTexture;
import net.minecraft.src.AbstractTexture;
import net.minecraft.src.ResourceManager;

public class DefaultTexture extends AbstractTexture {
    public DefaultTexture() {
        this.loadTexture(null);
    }

    public void loadTexture(ResourceManager manager) {
        int[] aint = ShadersTex.createAIntImage(1, -1);
        ShadersTex.setupTexture(((IShaderTexture) this).shadermod$getMultiTexID(), aint, 1, 1, false, false);
    }
}
