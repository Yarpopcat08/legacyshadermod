package me.andreasmelone.legacyshadermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import net.minecraft.src.Tessellator;

@Mixin(Tessellator.class)
public interface TessellatorAccessor {
    @Accessor("byteBuffer")
    ByteBuffer getBuffer();

    @Accessor("byteBuffer")
    void setBuffer(ByteBuffer buffer);

    @Accessor("intBuffer")
    IntBuffer getBufferInt();

    @Accessor("intBuffer")
    void setBufferInt(IntBuffer bufferInt);

    @Accessor("floatBuffer")
    FloatBuffer getBufferFloat();

    @Accessor("floatBuffer")
    void setBufferFloat(FloatBuffer bufferFloat);

    @Accessor("shortBuffer")
    ShortBuffer getBufferShort();

    @Accessor("shortBuffer")
    void setBufferShort(ShortBuffer bufferShort);

    @Accessor("rawBuffer")
    int[] getArray();

    @Accessor("rawBuffer")
    void setArray(int[] array);

    @Accessor("vertexCount")
    int getCount();

    @Accessor("vertexCount")
    void setCount(int count);

    @Accessor("textureU")
    double getU();

    @Accessor("textureU")
    void setU(double u);

    @Accessor("textureV")
    double getV();

    @Accessor("textureV")
    void setV(double v);

    @Accessor("brightness")
    int getLight();

    @Accessor("brightness")
    void setLight1(int light);

    @Accessor("color")
    int getColor();

    @Accessor("color")
    void setColor(int color);

    @Accessor("hasColor")
    boolean hasColor();

    @Accessor("hasColor")
    void setHasColor(boolean hasColor);

    @Accessor("hasTexture")
    boolean hasTexture();

    @Accessor("hasTexture")
    void setHasTexture(boolean hasTexture);

    @Accessor("hasBrightness")
    boolean hasLight();

    @Accessor("hasBrightness")
    void setHasLight(boolean hasLight);

    @Accessor("hasNormals")
    boolean hasNormal();

    @Accessor("hasNormals")
    void setHasNormal(boolean hasNormal);

    @Accessor("rawBufferIndex")
    int getArrayIdx();

    @Accessor("rawBufferIndex")
    void setArrayIdx(int arrayIdx);

    @Accessor("addedVertices")
    int getVertexCount();

    @Accessor("addedVertices")
    void setVertexCount(int vertexCount);

    @Accessor("isColorDisabled")
    boolean getFixedColor();

    @Accessor("isColorDisabled")
    void setFixedColor(boolean fixedColor);

    @Accessor("drawMode")
    int getFormat();

    @Accessor("drawMode")
    void setFormat(int format);

    @Accessor("xOffset")
    double getOffsetX();

    @Accessor("xOffset")
    void setOffsetX(double offsetX);

    @Accessor("yOffset")
    double getOffsetY();

    @Accessor("yOffset")
    void setOffsetY(double offsetY);

    @Accessor("zOffset")
    double getOffsetZ();

    @Accessor("zOffset")
    void setOffsetZ(double offsetZ);

    @Accessor("normal")
    int getNormal();

    @Accessor("normal")
    void setNormal(int normal);

    @Accessor("isDrawing")
    boolean isTessellating();

    @Accessor("isDrawing")
    void setTessellating(boolean tessellating);

    @Accessor("useVBO")
    boolean getField1940();

    @Accessor("useVBO")
    void setField1940(boolean field1940);

    @Accessor("vertexBuffers")
    IntBuffer getVboBuffer();

    @Accessor("vertexBuffers")
    void setVboBuffer(IntBuffer vboBuffer);

    @Accessor("vboIndex")
    int getField1942();

    @Accessor("vboIndex")
    void setField1942(int field1942);

    @Accessor("vboCount")
    int getField1943();

    @Accessor("vboCount")
    void setField1943(int field1943);

    @Accessor("bufferSize")
    int getBufferCapacity();

    @Accessor("bufferSize")
    void setBufferCapacity(int bufferCapacity);

    @Invoker("reset")
    void callReset();
}
