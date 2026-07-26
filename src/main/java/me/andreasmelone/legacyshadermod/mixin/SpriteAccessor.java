package me.andreasmelone.legacyshadermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.src.AnimationMetadataSection;
import net.minecraft.src.TextureAtlasSprite;

@Mixin(TextureAtlasSprite.class)
public interface SpriteAccessor {
    @Accessor("iconName")
    String shadermod$getName();

    @Accessor("framesTextureData")
    List shadermod$getFrames();

    @Accessor("framesTextureData")
    void shadermod$setFrames(List frames);

    @Accessor("animationMetadata")
    AnimationMetadataSection shadermod$getMeta();

    @Accessor("animationMetadata")
    void shadermod$setMeta(AnimationMetadataSection meta);

    @Accessor("rotated")
    boolean shadermod$getRotation();

    @Accessor("rotated")
    void shadermod$setRotation(boolean rotation);

    @Accessor("originX")
    int shadermod$getX();

    @Accessor("originX")
    void shadermod$setX(int x);

    @Accessor("originY")
    int shadermod$getY();

    @Accessor("originY")
    void shadermod$setY(int y);

    @Accessor("width")
    int shadermod$getWidth();

    @Accessor("width")
    void shadermod$setWidth(int width);

    @Accessor("height")
    int shadermod$getHeight();

    @Accessor("height")
    void shadermod$setHeight(int height);

    @Accessor("minU")
    float shadermod$getUMin();

    @Accessor("minU")
    void shadermod$setUMin(float uMin);

    @Accessor("maxU")
    float shadermod$getUMax();

    @Accessor("maxU")
    void shadermod$setUMax(float uMax);

    @Accessor("minV")
    float shadermod$getVMin();

    @Accessor("minV")
    void shadermod$setVMin(float vMin);

    @Accessor("maxV")
    float shadermod$getVMax();

    @Accessor("maxV")
    void shadermod$setVMax(float vMax);

    @Accessor("frameCounter")
    int shadermod$getFrameIndex();

    @Accessor("frameCounter")
    void shadermod$setFrameIndex(int frameIndex);

    @Accessor("tickCounter")
    int shadermod$getFrameTicks();

    @Accessor("tickCounter")
    void shadermod$setFrameTicks(int frameTicks);
}
