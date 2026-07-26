package me.andreasmelone.legacyshadermod.mixin;

import net.minecraft.src.ModelBase;
import net.minecraft.src.RendererLivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RendererLivingEntity.class)
public interface LivingEntityRendererAccessor {
    @Accessor("mainModel")
    ModelBase getModel();

    @Accessor("mainModel")
    void setModel(ModelBase model);

    @Accessor("renderPassModel")
    ModelBase getField_6504();

    @Accessor("renderPassModel")
    void setField_6504(ModelBase field_6504);
}
