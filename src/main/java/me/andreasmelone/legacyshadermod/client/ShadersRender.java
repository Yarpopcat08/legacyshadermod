package me.andreasmelone.legacyshadermod.client;

import net.minecraft.client.render.CullingCameraView;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.lwjgl.opengl.GL11;

public class ShadersRender {
    public static void setFrustrumPosition(CullingCameraView frustrum, double x, double y, double z) {
        frustrum.setPos(x, y, z);
    }

    public static void clipRenderersByFrustrum(WorldRenderer renderGlobal, CullingCameraView frustrum, float par2) {
        Shaders.checkGLError("pre clip");
        if (!Shaders.isShadowPass || Shaders.configShadowClipFrustrum) {
            renderGlobal.method_1373(frustrum, par2);
            Shaders.checkGLError("clip");
        }
    }

    public static void renderItemFP(HeldItemRenderer itemRenderer, float par1) {
        GL11.glDepthFunc(GL11.GL_GEQUAL);
        GL11.glPushMatrix();
        itemRenderer.renderArmHoldingItem(par1);
        GL11.glPopMatrix();
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        itemRenderer.renderArmHoldingItem(par1);
    }
}
