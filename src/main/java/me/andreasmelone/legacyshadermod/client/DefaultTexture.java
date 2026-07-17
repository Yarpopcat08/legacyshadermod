package me.andreasmelone.legacyshadermod.client;

import me.andreasmelone.legacyshadermod.mixinif.IShaderTexture;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.resource.ResourceManager;

public class DefaultTexture extends AbstractTexture {
    public DefaultTexture() {
        this.load(null);
    }

    public void load(ResourceManager manager) {
        int[] aint = ShadersTex.createAIntImage(1, -1);
        ShadersTex.setupTexture(((IShaderTexture) this).shadermod$getMultiTexID(), aint, 1, 1, false, false);
    }
}
