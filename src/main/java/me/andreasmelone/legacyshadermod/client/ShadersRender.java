package me.andreasmelone.legacyshadermod.client;

import net.minecraft.src.Frustrum;
import net.minecraft.src.ItemRenderer;
import net.minecraft.src.RenderGlobal;
import org.lwjgl.opengl.GL11;

public class ShadersRender {
    public static void setFrustrumPosition(Frustrum frustrum, double x, double y, double z) {
        frustrum.setPosition(x, y, z);
    }

    public static void clipRenderersByFrustrum(RenderGlobal renderGlobal, Frustrum frustrum, float par2) {
        Shaders.checkGLError("pre clip");
        if (!Shaders.isShadowPass || Shaders.configShadowClipFrustrum) {
            renderGlobal.clipRenderersByFrustum(frustrum, par2);
            Shaders.checkGLError("clip");
        }
    }

    public static void renderItemFP(ItemRenderer itemRenderer, float par1) {
        GL11.glDepthFunc(GL11.GL_GEQUAL);
        GL11.glPushMatrix();
        itemRenderer.renderItemInFirstPerson(par1);
        GL11.glPopMatrix();
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        itemRenderer.renderItemInFirstPerson(par1);
    }
}
