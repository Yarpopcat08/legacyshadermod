package me.andreasmelone.legacyshadermod.mixin;

import net.minecraft.client.resource.AnimationMetadata;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Sprite.class)
public interface SpriteAccessor {
    @Accessor("name")
    String shadermod$getName();

    @Accessor("frames")
    List shadermod$getFrames();

    @Accessor("frames")
    void shadermod$setFrames(List frames);

    @Accessor("meta")
    AnimationMetadata shadermod$getMeta();

    @Accessor("meta")
    void shadermod$setMeta(AnimationMetadata meta);

    @Accessor("rotation")
    boolean shadermod$getRotation();

    @Accessor("rotation")
    void shadermod$setRotation(boolean rotation);

    @Accessor("x")
    int shadermod$getX();

    @Accessor("x")
    void shadermod$setX(int x);

    @Accessor("y")
    int shadermod$getY();

    @Accessor("y")
    void shadermod$setY(int y);

    @Accessor("width")
    int shadermod$getWidth();

    @Accessor("width")
    void shadermod$setWidth(int width);

    @Accessor("height")
    int shadermod$getHeight();

    @Accessor("height")
    void shadermod$setHeight(int height);

    @Accessor("uMin")
    float shadermod$getUMin();

    @Accessor("uMin")
    void shadermod$setUMin(float uMin);

    @Accessor("uMax")
    float shadermod$getUMax();

    @Accessor("uMax")
    void shadermod$setUMax(float uMax);

    @Accessor("vMin")
    float shadermod$getVMin();

    @Accessor("vMin")
    void shadermod$setVMin(float vMin);

    @Accessor("vMax")
    float shadermod$getVMax();

    @Accessor("vMax")
    void shadermod$setVMax(float vMax);

    @Accessor("frameIndex")
    int shadermod$getFrameIndex();

    @Accessor("frameIndex")
    void shadermod$setFrameIndex(int frameIndex);

    @Accessor("frameTicks")
    int shadermod$getFrameTicks();

    @Accessor("frameTicks")
    void shadermod$setFrameTicks(int frameTicks);
}
