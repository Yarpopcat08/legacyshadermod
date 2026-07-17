package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.GuiShaders;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SettingsScreen.class)
public abstract class SettingsScreenMixin extends Screen {
    @Final
    @Shadow
    private GameOptions options;

    @ModifyConstant(method = "init", constant = @Constant(intValue = 150, ordinal = 2))
    private int modifyLanguageButtonWidth(int original) {
        return original - 20 - 57;
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addShadersButton(CallbackInfo ci) {
        this.buttons.add(new ButtonWidget(190, this.width / 2 - 152 + 77, this.height / 6 + 120 - 6, 73, 20, "Shaders..."));
    }

    @Inject(method = "buttonClicked", at = @At("HEAD"))
    private void onButtonClicked(ButtonWidget button, CallbackInfo ci) {
        if (button.id == 190) {
            this.client.options.save();
            this.client.setScreen(new GuiShaders((SettingsScreen) (Object) this, this.options));
        }
    }
}
