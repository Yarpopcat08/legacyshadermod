package me.andreasmelone.legacyshadermod.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.Util;
import org.lwjgl.Sys;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class GuiShaders extends Screen {
    protected Screen parentGui;
    private int updateTimer = -1;
    public boolean needReinit = false;
    public boolean needRefreshResource = false;
    private GuiSlotShaders shaderList;

    public GuiShaders(Screen par1GuiScreen, GameOptions par2GameSettings) {
        this.parentGui = par1GuiScreen;
    }

    private static String toStringOnOff(boolean value) {
        return value ? "On" : "Off";
    }

    public void init() {
        if (Shaders.shadersConfig == null) {
            Shaders.loadConfig();
        }

        List buttonList = this.buttons;
        int width = this.width;
        int height = this.height;
        buttonList.add(new ButtonWidget(15, width * 3 / 4 - 60, 70, 160, 18, "RenderResMul: " + String.format("%.04f", Shaders.configRenderResMul)));
        buttonList.add(new ButtonWidget(16, width * 3 / 4 - 60, 90, 160, 18, "ShadowResMul: " + String.format("%.04f", Shaders.configShadowResMul)));
        buttonList.add(new ButtonWidget(10, width * 3 / 4 - 60, 110, 160, 18, "HandDepth: " + String.format("%.04f", Shaders.configHandDepthMul)));
        buttonList.add(new ButtonWidget(9, width * 3 / 4 - 60, 130, 160, 18, "CloudShadow: " + toStringOnOff(Shaders.configCloudShadow)));
        buttonList.add(new ButtonWidget(14, width * 3 / 4 - 60, 150, 160, 18, "ShadowClipFrustrum: " + toStringOnOff(Shaders.configShadowClipFrustrum)));
        buttonList.add(new ButtonWidget(4, width * 3 / 4 - 60, 170, 160, 18, "tweakBlockDamage: " + toStringOnOff(Shaders.configTweakBlockDamage)));
        buttonList.add(new ButtonWidget(19, width * 3 / 4 - 60, 190, 160, 18, "OldLighting: " + toStringOnOff(Shaders.configOldLighting)));
        buttonList.add(new ButtonWidget(6, width * 3 / 4 - 60, height - 25, 160, 20, "Done"));
        buttonList.add(new ButtonWidget(5, width / 4 - 80, height - 25, 160, 20, "Open shaderpacks folder"));
        this.shaderList = new GuiSlotShaders(this);
        this.shaderList.setButtonIds(7, 8);
        this.needReinit = false;
    }

    protected void buttonClicked(ButtonWidget button) {
        if (button.active) {
            switch (button.id) {
                case 4:
                    Shaders.configTweakBlockDamage = !Shaders.configTweakBlockDamage;
                    button.message = "tweakBlockDamage: " + toStringOnOff(Shaders.configTweakBlockDamage);
                    break;
                case 5:
                    switch (Util.method_6318()) {
                        case MACOS:
                            try {
                                Runtime.getRuntime().exec(new String[]{"/usr/bin/open", Shaders.shaderpacksdir.getAbsolutePath()});
                                return;
                            } catch (IOException var81) {
                                var81.printStackTrace();
                                break;
                            }
                        case WINDOWS:
                            String var2 = String.format("cmd.exe /C start \"Open file\" \"%s\"", Shaders.shaderpacksdir.getAbsolutePath());

                            try {
                                Runtime.getRuntime().exec(var2);
                                return;
                            } catch (IOException var7) {
                                var7.printStackTrace();
                            }
                    }

                    boolean var8 = false;

                    try {
                        Class var3 = Class.forName("java.awt.Desktop");
                        Object var4 = var3.getMethod("getDesktop").invoke((Object) null);
                        var3.getMethod("browse", URI.class).invoke(var4, new File(this.client.runDirectory, Shaders.shaderpacksdirname).toURI());
                    } catch (Throwable var6) {
                        var6.printStackTrace();
                        var8 = true;
                    }

                    if (var8) {
                        System.out.println("Opening via system class!");
                        Sys.openURL("file://" + Shaders.shaderpacksdir.getAbsolutePath());
                    }
                    break;
                case 6:
                    new File(Shaders.shadersdir, "current.cfg");

                    try {
                        Shaders.storeConfig();
                    } catch (Exception var5) {
                    }

                    if (this.needReinit) {
                        this.needReinit = false;
                        Shaders.loadShaderPack();
                        Shaders.uninit();
                        this.client.worldRenderer.reload();
                    }

                    if (this.needRefreshResource) {
                        this.needRefreshResource = false;
                        this.client.reloadResources();
                    }

                    this.client.setScreen(this.parentGui);
                    break;
                case 7:
                case 8:
                default:
                    this.shaderList.buttonClicked(button);
                    break;
                case 9:
                    Shaders.configCloudShadow = !Shaders.configCloudShadow;
                    button.message = "CloudShadow: " + toStringOnOff(Shaders.configCloudShadow);
                    break;
                case 10:
                    float val = Shaders.configHandDepthMul;
                    float[] choices = new float[]{0.0625F, 0.125F, 0.25F, 0.5F, 1.0F};
                    int i;
                    if (!hasShiftDown()) {
                        i = 0;

                        while (i < choices.length && choices[i] <= val) {
                            i++;
                        }

                        if (i == choices.length) {
                            i = 0;
                        }
                    } else {
                        i = choices.length - 1;

                        while (i >= 0 && val <= choices[i]) {
                            i--;
                        }

                        if (i < 0) {
                            i = choices.length - 1;
                        }
                    }

                    Shaders.configHandDepthMul = choices[i];
                    button.message = "HandDepth: " + String.format("%.4f", Shaders.configHandDepthMul);
                    break;
                case 11:
                    Shaders.configTexMinFilB = (Shaders.configTexMinFilB + 1) % 3;
                    Shaders.configTexMinFilN = Shaders.configTexMinFilS = Shaders.configTexMinFilB;
                    button.message = "Tex Min: " + Shaders.texMinFilDesc[Shaders.configTexMinFilB];
                    ShadersTex.updateTextureMinMagFilter();
                    break;
                case 12:
                    Shaders.configTexMagFilN = (Shaders.configTexMagFilN + 1) % 2;
                    button.message = "Tex_n Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilN];
                    ShadersTex.updateTextureMinMagFilter();
                    break;
                case 13:
                    Shaders.configTexMagFilS = (Shaders.configTexMagFilS + 1) % 2;
                    button.message = "Tex_s Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilS];
                    ShadersTex.updateTextureMinMagFilter();
                    break;
                case 14:
                    Shaders.configShadowClipFrustrum = !Shaders.configShadowClipFrustrum;
                    button.message = "ShadowClipFrustrum: " + toStringOnOff(Shaders.configShadowClipFrustrum);
                    ShadersTex.updateTextureMinMagFilter();
                    break;
                case 15:
                    val = Shaders.configRenderResMul;
                    choices = new float[]{0.25F, 0.33333334F, 0.5F, 0.70710677F, 1.0F, 1.4142135F, 2.0F};
                    if (!hasShiftDown()) {
                        i = 0;

                        while (i < choices.length && choices[i] <= val) {
                            i++;
                        }

                        if (i == choices.length) {
                            i = 0;
                        }
                    } else {
                        i = choices.length - 1;

                        while (i >= 0 && val <= choices[i]) {
                            i--;
                        }

                        if (i < 0) {
                            i = choices.length - 1;
                        }
                    }

                    Shaders.configRenderResMul = choices[i];
                    button.message = "RenderResMul: " + String.format("%.4f", Shaders.configRenderResMul);
                    Shaders.scheduleResize();
                    break;
                case 16:
                    val = Shaders.configShadowResMul;
                    choices = new float[]{0.25F, 0.33333334F, 0.5F, 0.70710677F, 1.0F, 1.4142135F, 2.0F, 3.0F, 4.0F};
                    if (!hasShiftDown()) {
                        i = 0;

                        while (i < choices.length && choices[i] <= val) {
                            i++;
                        }

                        if (i == choices.length) {
                            i = 0;
                        }
                    } else {
                        i = choices.length - 1;

                        while (i >= 0 && val <= choices[i]) {
                            i--;
                        }

                        if (i < 0) {
                            i = choices.length - 1;
                        }
                    }

                    Shaders.configShadowResMul = choices[i];
                    button.message = "ShadowResMul: " + String.format("%.4f", Shaders.configShadowResMul);
                    Shaders.scheduleResizeShadow();
                    break;
                case 17:
                    Shaders.configNormalMap = !Shaders.configNormalMap;
                    button.message = "NormapMap: " + toStringOnOff(Shaders.configNormalMap);
                    this.needRefreshResource = true;
                    break;
                case 18:
                    Shaders.configSpecularMap = !Shaders.configSpecularMap;
                    button.message = "SpecularMap: " + toStringOnOff(Shaders.configSpecularMap);
                    this.needRefreshResource = true;
                    break;
                case 19:
                    Shaders.configOldLighting = !Shaders.configOldLighting;
                    button.message = "OldLighting: " + toStringOnOff(Shaders.configOldLighting);
                    Shaders.updateBlockLightLevel();
                    this.client.worldRenderer.reload();
            }
        }
    }

    public void render(int mouseX, int mouseY, float tickDelta) {
        this.shaderList.render(mouseX, mouseY, tickDelta);
        if (this.updateTimer <= 0) {
            this.shaderList.updateList();
            this.updateTimer += 20;
        }

        this.drawCenteredString(this.textRenderer, "Shaders ", this.width / 2, 16, 16777215);
        this.drawCenteredString(this.textRenderer, " v2.2.3", this.width - 40, 10, 8421504);
        super.render(mouseX, mouseY, tickDelta);
    }

    public void tick() {
        super.tick();
        this.updateTimer--;
    }

    public MinecraftClient getMc() {
        return this.client;
    }

    public void drawCenteredString(String par1, int par2, int par3, int par4) {
        this.drawCenteredString(this.textRenderer, par1, par2, par3, par4);
    }
}
