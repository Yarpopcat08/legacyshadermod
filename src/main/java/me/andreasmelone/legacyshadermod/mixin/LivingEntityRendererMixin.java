package me.andreasmelone.legacyshadermod.mixin;

import me.andreasmelone.legacyshadermod.client.Shaders;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ModelBase;
import net.minecraft.src.OpenGlHelper;
import net.minecraft.src.Render;
import net.minecraft.src.RendererLivingEntity;
import net.minecraft.src.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RendererLivingEntity.class)
public abstract class LivingEntityRendererMixin extends Render {
    @Shadow
    protected ModelBase mainModel;
    @Shadow
    protected ModelBase renderPassModel;

    @Shadow
    protected abstract int getColorMultiplier(EntityLivingBase entity, float brightness, float tickDelta);

    @Shadow
    protected abstract float renderSwingProgress(EntityLivingBase livingEntity, float f);

    @Shadow
    protected abstract float interpolateRotation(float f, float g, float h);

    @Shadow
    protected abstract void renderLivingAt(EntityLivingBase livingEntity, double d, double e, double f);

    @Shadow
    protected abstract float handleRotationFloat(EntityLivingBase entity, float f);

    @Shadow
    protected abstract void rotateCorpse(EntityLivingBase entity, float f, float g, float h);

    @Shadow
    protected abstract void preRenderCallback(EntityLivingBase entity, float tickDelta);

    @Shadow
    protected abstract void renderModel(EntityLivingBase entity, float f, float g, float h, float i, float j, float k);

    @Shadow
    protected abstract int shouldRenderPass(EntityLivingBase livingEntity, int i, float f);

    @Shadow
    protected abstract void func_82408_c(EntityLivingBase livingEntity, int i, float f);

    @Shadow
    @Final
    private static ResourceLocation RES_ITEM_GLINT;

    @Shadow
    protected abstract void renderEquippedItems(EntityLivingBase livingEntity, float f);

    @Shadow
    protected abstract int inheritRenderPass(EntityLivingBase livingEntity, int i, float f);

    @Shadow
    protected abstract void passSpecialRender(EntityLivingBase livingEntity, double d, double e, double f);

    /**
     * @author AndreasMelone
     * @reason this is just way too difficult do with pure injects, so I do this using an overwrite
     */
    @Overwrite
    public void doRenderLiving(EntityLivingBase var1, double var2, double var4, double var6, float var8, float var9) {
        if (Shaders.useEntityHurtFlash) {
            Shaders.setEntityHurtFlash(var1.hurtTime <= 0 && var1.deathTime <= 0 ? 0 : 102, this.getColorMultiplier(var1, var1.getBrightness(var9), var9));
        }

        GL11.glPushMatrix();
        GL11.glDisable(2884);
        this.mainModel.onGround = this.renderSwingProgress(var1, var9);
        if (this.renderPassModel != null) {
            this.renderPassModel.onGround = this.mainModel.onGround;
        }

        this.mainModel.isRiding = var1.isRiding();
        if (this.renderPassModel != null) {
            this.renderPassModel.isRiding = this.mainModel.isRiding;
        }

        this.mainModel.isChild = var1.isChild();
        if (this.renderPassModel != null) {
            this.renderPassModel.isChild = this.mainModel.isChild;
        }

        try {
            float var10 = this.interpolateRotation(var1.prevRenderYawOffset, var1.renderYawOffset, var9);
            float var11 = this.interpolateRotation(var1.prevRotationYawHead, var1.rotationYawHead, var9);
            if (var1.isRiding() && var1.ridingEntity instanceof EntityLivingBase) {
                EntityLivingBase var12 = (EntityLivingBase) var1.ridingEntity;
                var10 = this.interpolateRotation(var12.prevRenderYawOffset, var12.renderYawOffset, var9);
                float var13 = MathHelper.wrapAngleTo180_float(var11 - var10);
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

            float var27 = var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var9;
            this.renderLivingAt(var1, var2, var4, var6);
            float var28 = this.handleRotationFloat(var1, var9);
            this.rotateCorpse(var1, var28, var10, var9);
            float var14 = 0.0625F;
            GL11.glEnable(32826);
            GL11.glScalef(-1.0F, -1.0F, 1.0F);
            this.preRenderCallback(var1, var9);
            GL11.glTranslatef(0.0F, -24.0F * var14 - 0.0078125F, 0.0F);
            float var15 = var1.prevLimbSwingAmount + (var1.limbSwingAmount - var1.prevLimbSwingAmount) * var9;
            float var16 = var1.limbSwing - var1.limbSwingAmount * (1.0F - var9);
            if (var1.isChild()) {
                var16 *= 3.0F;
            }

            if (var15 > 1.0F) {
                var15 = 1.0F;
            }

            GL11.glEnable(3008);
            this.mainModel.setLivingAnimations(var1, var16, var15, var9);
            this.renderModel(var1, var16, var15, var28, var11 - var10, var27, var14);

            for (int var17 = 0; var17 < 4; var17++) {
                int var18 = this.shouldRenderPass(var1, var17, var9);
                if (var18 > 0) {
                    this.renderPassModel.setLivingAnimations(var1, var16, var15, var9);
                    this.renderPassModel.render(var1, var16, var15, var28, var11 - var10, var27, var14);
                    if ((var18 & 240) == 16) {
                        this.func_82408_c(var1, var17, var9);
                        this.renderPassModel.render(var1, var16, var15, var28, var11 - var10, var27, var14);
                    }

                    if ((var18 & 15) == 15) {
                        float var19 = var1.ticksExisted + var9;
                        this.bindTexture(RES_ITEM_GLINT);
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
                            this.renderPassModel.render(var1, var16, var15, var28, var11 - var10, var27, var14);
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
            this.renderEquippedItems(var1, var9);
            if (!Shaders.useEntityHurtFlash) {
                float var29 = var1.getBrightness(var9);
                int var30 = this.getColorMultiplier(var1, var29, var9);
                OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                GL11.glDisable(3553);
                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
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
                        this.mainModel.render(var1, var16, var15, var28, var11 - var10, var27, var14);

                        for (int var31 = 0; var31 < 4; var31++) {
                            if (this.inheritRenderPass(var1, var31, var9) >= 0) {
                                GL11.glColor4f(var29, 0.0F, 0.0F, 0.4F);
                                this.renderPassModel.render(var1, var16, var15, var28, var11 - var10, var27, var14);
                            }
                        }
                    }

                    if ((var30 >> 24 & 0xFF) > 0) {
                        float var32 = (var30 >> 16 & 0xFF) / 255.0F;
                        float var33 = (var30 >> 8 & 0xFF) / 255.0F;
                        float var34 = (var30 & 0xFF) / 255.0F;
                        float var35 = (var30 >> 24 & 0xFF) / 255.0F;
                        GL11.glColor4f(var32, var33, var34, var35);
                        this.mainModel.render(var1, var16, var15, var28, var11 - var10, var27, var14);

                        for (int var36 = 0; var36 < 4; var36++) {
                            if (this.inheritRenderPass(var1, var36, var9) >= 0) {
                                GL11.glColor4f(var32, var33, var34, var35);
                                this.renderPassModel.render(var1, var16, var15, var28, var11 - var10, var27, var14);
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

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnable(3553);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        Shaders.enableLightmap();
        GL11.glEnable(2884);
        GL11.glPopMatrix();
        this.passSpecialRender(var1, var2, var4, var6);
    }
}
