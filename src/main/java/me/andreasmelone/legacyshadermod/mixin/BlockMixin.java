package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.block.Block;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(
            method = "method_465",
            at = @At("RETURN"),
            cancellable = true
    )
    private void modifyAoLight(BlockView i, int j, int k, int par4, CallbackInfoReturnable<Float> cir) {
        if (cir.getReturnValueF() == 0.2F) {
            cir.setReturnValue(Shaders.blockAoLight);
        }
    }
}
