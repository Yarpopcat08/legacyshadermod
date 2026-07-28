package me.andreasmelone.legacyshadermod.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.lwjgl.opengl.GL11;

@Mixin(GuiIngame.class)
public abstract class VanillaInGameHudMixin extends Gui {
    @Shadow
    @Final
    private Minecraft mc;

    @WrapOperation(
            method = "renderGameOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ScaledResolution;getScaledWidth()I")
    )
    public int injectToGetWindowWidth(ScaledResolution instance, Operation<Integer> original, @Share("windowwidth") LocalIntRef windowWidth) {
        int windowWidthOriginal = original.call(instance);
        windowWidth.set(windowWidthOriginal);
        return windowWidthOriginal;
    }

    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/class_371;method_993(Lnet/minecraft/class_370;Ljava/lang/String;III)V",
                    ordinal = 1
            )
    )
    public void renderShaderName(float inScreen, boolean mouseX, int mouseY, int par4, CallbackInfo ci, @Share("windowwidth") LocalIntRef windowWidth) {
        if(Shaders.getShaderPack() != null) {
            FontRenderer font = this.mc.fontRenderer;
            String text = "Shaderpack: " + Shaders.getShaderPack();
            this.drawString(
                    font, text,
                    windowWidth.get() - font.getStringWidth(text) - 2, 22,
                    0xFFFFFFFF
            );
        }
    }

    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/class_371;method_993(Lnet/minecraft/class_370;Ljava/lang/String;III)V",
                    ordinal = 1
            )
    )
    public void renderGpuData(float inScreen, boolean mouseX, int mouseY, int par4, CallbackInfo ci, @Share("windowwidth") LocalIntRef windowWidth) {
        if(Shaders.getShaderPack() != null) {
            FontRenderer font = this.mc.fontRenderer;
            String text = "GPU: " + GL11.glGetString(GL11.GL_RENDERER);
            this.drawString(
                    font, text,
                    windowWidth.get() - font.getStringWidth(text) - 2, 32,
                    0xFFFFFFFF
            );
            String data = GL11.glGetString(GL11.GL_VERSION) + " " + GL11.glGetString(GL11.GL_VENDOR);
            this.drawString(
                    font, data,

                    windowWidth.get() - font.getStringWidth(data) - 2, 42,
                    0xFFFFFFFF
            );
        }
    }
}
