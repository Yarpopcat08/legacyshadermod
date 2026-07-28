package me.andreasmelone.legacyshadermod.mixin.btw;

import api.client.debug.DebugInfoSection;
import api.client.debug.DebugRegistryUtils;
import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Minecraft.class)
public class BTWMinecraftMixin {
    @Inject(
            method = "startGame",
            at = @At(
                    value = "INVOKE",
                    target = "Lapi/client/debug/DebugRegistry;registerAll()V",
                    shift = At.Shift.AFTER
            )
    )
    public void registerDebug(CallbackInfo ci) {
        DebugInfoSection section = DebugRegistryUtils.registerSection(new ResourceLocation("shadersmod", "shaders"), DebugRegistryUtils.Side.RIGHT);
        section.addEntry((mc, isExtendedDebug) -> {
            return Optional.of("Shaderpack: §e" + Shaders.getShaderPack());
        });
        section.addEntry((mc, isExtendedDebug) -> {
            return Optional.of(GL11.glGetString(GL11.GL_VERSION) + " §e" + GL11.glGetString(GL11.GL_VENDOR));
        });
    }
}
