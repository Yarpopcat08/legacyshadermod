package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.GuiShaders;
import net.minecraft.src.GameSettings;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiOptions;
import net.minecraft.src.GuiScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiOptions.class)
public abstract class SettingsScreenMixin extends GuiScreen {
    @Final
    @Shadow
    private GameSettings options;

    @ModifyConstant(method = "initGui", constant = @Constant(intValue = 150, ordinal = 2))
    private int modifyLanguageButtonWidth(int original) {
        return original - 20 - 57;
    }

    @Inject(method = "initGui", at = @At("RETURN"))
    private void addShadersButton(CallbackInfo ci) {
        this.buttonList.add(new GuiButton(190, this.width / 2 - 152 + 77, this.height / 6 + 120 - 6, 73, 20, "Shaders..."));
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void onButtonClicked(GuiButton button, CallbackInfo ci) {
        if (button.id == 190) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiShaders((GuiOptions) (Object) this, this.options));
        }
    }
}
