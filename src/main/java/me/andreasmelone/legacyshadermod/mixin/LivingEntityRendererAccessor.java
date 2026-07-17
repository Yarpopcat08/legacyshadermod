package me.andreasmelone.legacyshadermod.mixin;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor {
    @Accessor("model")
    EntityModel getModel();

    @Accessor("model")
    void setModel(EntityModel model);

    @Accessor("field_6504")
    EntityModel getField_6504();

    @Accessor("field_6504")
    void setField_6504(EntityModel field_6504);
}
