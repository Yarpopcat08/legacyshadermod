package me.andreasmelone.legacyshadermod.client;

import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.render.Tessellator;

import java.util.ArrayList;

class GuiSlotShaders extends ListWidget {
    private ArrayList shaderslist;
    private int scrollBarX;
    final GuiShaders shadersGui;

    public GuiSlotShaders(GuiShaders par1GuiShaders) {
        super(par1GuiShaders.getMc(), par1GuiShaders.width / 2 + 20, par1GuiShaders.height, 40, par1GuiShaders.height - 70, 16);
        this.scrollBarX = par1GuiShaders.width / 2 + 14;
        this.shadersGui = par1GuiShaders;
        this.shaderslist = Shaders.listofShaders();
    }

    public void updateList() {
        this.shaderslist = Shaders.listofShaders();
    }

    protected int getEntryCount() {
        return this.shaderslist.size();
    }

    protected void method_1057(int par1, boolean par2) {
        Shaders.setShaderPack((String) this.shaderslist.get(par1));
        this.shadersGui.needReinit = false;
        Shaders.loadShaderPack();
        Shaders.uninit();
    }

    protected boolean isEntrySelected(int index) {
        return ((String) this.shaderslist.get(index)).equals(Shaders.currentshadername);
    }

    protected int getScrollbarPosition() {
        return this.scrollBarX;
    }

    protected int getMaxPosition() {
        return this.getEntryCount() * 18;
    }

    protected void renderBackground() {
        this.shadersGui.renderBackground();
    }

    protected void func_77206_b(int par1, int par2, int par3, int par4) {
    }

    protected void drawContainerBackground(Tessellator tess) {
    }

    protected void method_1055(int par1, int par2, int par3, int par4, Tessellator par5Tessellator) {
        this.shadersGui.drawCenteredString((String) this.shaderslist.get(par1), this.scrollBarX / 2, par3 + 1, 16777215);
    }
}
