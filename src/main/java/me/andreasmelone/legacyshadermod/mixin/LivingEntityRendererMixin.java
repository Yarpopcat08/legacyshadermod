package me.andreasmelone.legacyshadermod.mixin;

import com.mojang.blaze3d.platform.GLX;
import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin extends EntityRenderer {
    @Shadow
    protected EntityModel model;
    @Shadow
    protected EntityModel field_6504;

    @Shadow
    protected abstract int method_5776(LivingEntity entity, float brightness, float tickDelta);

    @Shadow
    protected abstract float method_5787(LivingEntity livingEntity, float f);

    @Shadow
    protected abstract float method_5769(float f, float g, float h);

    @Shadow
    protected abstract void method_5772(LivingEntity livingEntity, double d, double e, double f);

    @Shadow
    protected abstract float method_5783(LivingEntity entity, float f);

    @Shadow
    protected abstract void method_5777(LivingEntity entity, float f, float g, float h);

    @Shadow
    protected abstract void scale(LivingEntity entity, float tickDelta);

    @Shadow
    protected abstract void renderModel(LivingEntity entity, float f, float g, float h, float i, float j, float k);

    @Shadow
    protected abstract int method_5779(LivingEntity livingEntity, int i, float f);

    @Shadow
    protected abstract void method_5786(LivingEntity livingEntity, int i, float f);

    @Shadow
    @Final
    private static Identifier field_6502;

    @Shadow
    protected abstract void method_5785(LivingEntity livingEntity, float f);

    @Shadow
    protected abstract int method_5784(LivingEntity livingEntity, int i, float f);

    @Shadow
    protected abstract void method_5782(LivingEntity livingEntity, double d, double e, double f);

    /**
     * @author AndreasMelone
     * @reason this is just way too difficult do with pure injects, so I do this using an overwrite
     */
    @Overwrite
    public void render(LivingEntity var1, double var2, double var4, double var6, float var8, float var9) {
        if (Shaders.useEntityHurtFlash) {
            Shaders.setEntityHurtFlash(var1.hurtTime <= 0 && var1.deathTime <= 0 ? 0 : 102, this.method_5776(var1, var1.getBrightnessAtEyes(var9), var9));
        }

        GL11.glPushMatrix();
        GL11.glDisable(2884);
        this.model.handSwingProgress = this.method_5787(var1, var9);
        if (this.field_6504 != null) {
            this.field_6504.handSwingProgress = this.model.handSwingProgress;
        }

        this.model.riding = var1.hasVehicle();
        if (this.field_6504 != null) {
            this.field_6504.riding = this.model.riding;
        }

        this.model.child = var1.isBaby();
        if (this.field_6504 != null) {
            this.field_6504.child = this.model.child;
        }

        try {
            float var10 = this.method_5769(var1.prevBodyYaw, var1.bodyYaw, var9);
            float var11 = this.method_5769(var1.prevHeadYaw, var1.headYaw, var9);
            if (var1.hasVehicle() && var1.vehicle instanceof LivingEntity) {
                LivingEntity var12 = (LivingEntity) var1.vehicle;
                var10 = this.method_5769(var12.prevBodyYaw, var12.bodyYaw, var9);
                float var13 = MathHelper.wrapDegrees(var11 - var10);
                if (var13 < -85.0F) {
                    var13 = -85.0F;
                }

                if (var13 >= 85.0F) {
                    var13 = 85.0F;
                }

                var10 = var11 - var13;
                if (var13 * var13 > 2500.0F) {
                    var10 += var13 * 0.2F;
                }
            }

            float var27 = var1.prevPitch + (var1.pitch - var1.prevPitch) * var9;
            this.method_5772(var1, var2, var4, var6);
            float var28 = this.method_5783(var1, var9);
            this.method_5777(var1, var28, var10, var9);
            float var14 = 0.0625F;
            GL11.glEnable(32826);
            GL11.glScalef(-1.0F, -1.0F, 1.0F);
            this.scale(var1, var9);
            GL11.glTranslatef(0.0F, -24.0F * var14 - 0.0078125F, 0.0F);
            float var15 = var1.field_6748 + (var1.field_6749 - var1.field_6748) * var9;
            float var16 = var1.field_6750 - var1.field_6749 * (1.0F - var9);
            if (var1.isBaby()) {
                var16 *= 3.0F;
            }

            if (var15 > 1.0F) {
                var15 = 1.0F;
            }

            GL11.glEnable(3008);
            this.model.animateModel(var1, var16, var15, var9);
            this.renderModel(var1, var16, var15, var28, var11 - var10, var27, var14);

            for (int var17 = 0; var17 < 4; var17++) {
                int var18 = this.method_5779(var1, var17, var9);
                if (var18 > 0) {
                    this.field_6504.animateModel(var1, var16, var15, var9);
                    this.field_6504.render(var1, var16, var15, var28, var11 - var10, var27, var14);
                    if ((var18 & 240) == 16) {
                        this.method_5786(var1, var17, var9);
                        this.field_6504.render(var1, var16, var15, var28, var11 - var10, var27, var14);
                    }

                    if ((var18 & 15) == 15) {
                        float var19 = var1.ticksAlive + var9;
                        this.bindTexture(field_6502);
                        GL11.glEnable(3042);
                        float var20 = 0.5F;
                        GL11.glColor4f(var20, var20, var20, 1.0F);
                        GL11.glDepthFunc(514);
                        GL11.glDepthMask(false);

                        for (int var21 = 0; var21 < 2; var21++) {
                            GL11.glDisable(2896);
                            float var22 = 0.76F;
                            GL11.glColor4f(0.5F * var22, 0.25F * var22, 0.8F * var22, 1.0F);
                            GL11.glBlendFunc(768, 1);
                            GL11.glMatrixMode(5890);
                            GL11.glLoadIdentity();
                            float var23 = var19 * (0.001F + var21 * 0.003F) * 20.0F;
                            float var24 = 0.33333334F;
                            GL11.glScalef(var24, var24, var24);
                            GL11.glRotatef(30.0F - var21 * 60.0F, 0.0F, 0.0F, 1.0F);
                            GL11.glTranslatef(0.0F, var23, 0.0F);
                            GL11.glMatrixMode(5888);
                            this.field_6504.render(var1, var16, var15, var28, var11 - var10, var27, var14);
                        }

                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        GL11.glMatrixMode(5890);
                        GL11.glDepthMask(true);
                        GL11.glLoadIdentity();
                        GL11.glMatrixMode(5888);
                        GL11.glEnable(2896);
                        GL11.glDisable(3042);
                        GL11.glDepthFunc(515);
                    }

                    GL11.glDisable(3042);
                    GL11.glEnable(3008);
                }
            }

            GL11.glDepthMask(true);
            Shaders.resetEntityHurtFlash();
            this.method_5785(var1, var9);
            if (!Shaders.useEntityHurtFlash) {
                float var29 = var1.getBrightnessAtEyes(var9);
                int var30 = this.method_5776(var1, var29, var9);
                GLX.gl13ActiveTexture(GLX.lightmapTextureUnit);
                GL11.glDisable(3553);
                GLX.gl13ActiveTexture(GLX.textureUnit);
                Shaders.disableLightmap();
                if ((var30 >> 24 & 0xFF) > 0 || var1.hurtTime > 0 || var1.deathTime > 0) {
                    GL11.glDisable(3553);
                    GL11.glDisable(3008);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glDepthFunc(514);
                    Shaders.beginLivingDamage();
                    if (var1.hurtTime > 0 || var1.deathTime > 0) {
                        GL11.glColor4f(var29, 0.0F, 0.0F, 0.4F);
                        this.model.render(var1, var16, var15, var28, var11 - var10, var27, var14);

                        for (int var31 = 0; var31 < 4; var31++) {
                            if (this.method_5784(var1, var31, var9) >= 0) {
                                GL11.glColor4f(var29, 0.0F, 0.0F, 0.4F);
                                this.field_6504.render(var1, var16, var15, var28, var11 - var10, var27, var14);
                            }
                        }
                    }

                    if ((var30 >> 24 & 0xFF) > 0) {
                        float var32 = (var30 >> 16 & 0xFF) / 255.0F;
                        float var33 = (var30 >> 8 & 0xFF) / 255.0F;
                        float var34 = (var30 & 0xFF) / 255.0F;
                        float var35 = (var30 >> 24 & 0xFF) / 255.0F;
                        GL11.glColor4f(var32, var33, var34, var35);
                        this.model.render(var1, var16, var15, var28, var11 - var10, var27, var14);

                        for (int var36 = 0; var36 < 4; var36++) {
                            if (this.method_5784(var1, var36, var9) >= 0) {
                                GL11.glColor4f(var32, var33, var34, var35);
                                this.field_6504.render(var1, var16, var15, var28, var11 - var10, var27, var14);
                            }
                        }
                    }

                    GL11.glDepthFunc(515);
                    Shaders.endLivingDamage();
                    GL11.glDisable(3042);
                    GL11.glEnable(3008);
                    GL11.glEnable(3553);
                }
            }

            GL11.glDisable(32826);
        } catch (Exception var25) {
            var25.printStackTrace();
        }

        GLX.gl13ActiveTexture(GLX.lightmapTextureUnit);
        GL11.glEnable(3553);
        GLX.gl13ActiveTexture(GLX.textureUnit);
        Shaders.enableLightmap();
        GL11.glEnable(2884);
        GL11.glPopMatrix();
        this.method_5782(var1, var2, var4, var6);
    }
}
