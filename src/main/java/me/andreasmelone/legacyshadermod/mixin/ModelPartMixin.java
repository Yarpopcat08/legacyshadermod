package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.mixinif.IModelPart;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelRenderer.class)
public class ModelPartMixin implements IModelPart {
    @Shadow
    private boolean compiled;

    @Shadow
    private int displayList;

    @Override
    public boolean shadermod$getCompiled() {
        return this.compiled;
    }

    @Override
    public int shadermod$getDisplayList() {
        return this.displayList;
    }

    @Override
    public void shadermod$resetDisplayList() {
        if (this.compiled) {
            GLAllocation.deleteDisplayLists(this.displayList);
            this.displayList = 0;
            this.compiled = false;
        }
    }
}
