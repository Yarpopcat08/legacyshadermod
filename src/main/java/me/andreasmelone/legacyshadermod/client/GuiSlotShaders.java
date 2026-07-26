package me.andreasmelone.legacyshadermod.client;

import java.util.ArrayList;
import net.minecraft.src.GuiSlot;
import net.minecraft.src.Tessellator;

class GuiSlotShaders extends GuiSlot {
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

    protected int getSize() {
        return this.shaderslist.size();
    }

    protected void elementClicked(int par1, boolean par2) {
        Shaders.setShaderPack((String) this.shaderslist.get(par1));
        this.shadersGui.needReinit = false;
        Shaders.loadShaderPack();
        Shaders.uninit();
    }

    protected boolean isSelected(int index) {
        return ((String) this.shaderslist.get(index)).equals(Shaders.currentshadername);
    }

    protected int getScrollBarX() {
        return this.scrollBarX;
    }

    protected int getContentHeight() {
        return this.getSize() * 18;
    }

    protected void drawBackground() {
        this.shadersGui.drawDefaultBackground();
    }

    protected void func_77206_b(int par1, int par2, int par3, int par4) {
    }

    protected void drawContainerBackground(Tessellator tess) {
    }

    protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5Tessellator) {
        this.shadersGui.drawCenteredString((String) this.shaderslist.get(par1), this.scrollBarX / 2, par3 + 1, 16777215);
    }
}
