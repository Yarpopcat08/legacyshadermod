package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.mixinif.IModelPart;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.util.GlAllocationUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelPart.class)
public class ModelPartMixin implements IModelPart {
    @Shadow
    private boolean compiledList;

    @Shadow
    private int glList;

    @Override
    public boolean shadermod$getCompiled() {
        return this.compiledList;
    }

    @Override
    public int shadermod$getDisplayList() {
        return this.glList;
    }

    @Override
    public void shadermod$resetDisplayList() {
        if (this.compiledList) {
            GlAllocationUtils.deleteSingletonList(this.glList);
            this.glList = 0;
            this.compiledList = false;
        }
    }
}
