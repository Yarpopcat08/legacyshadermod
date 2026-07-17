package me.andreasmelone.legacyshadermod.mixin;

import net.minecraft.client.render.Tessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

@Mixin(Tessellator.class)
public interface TessellatorAccessor {
    @Accessor("buffer")
    ByteBuffer getBuffer();

    @Accessor("buffer")
    void setBuffer(ByteBuffer buffer);

    @Accessor("bufferInt")
    IntBuffer getBufferInt();

    @Accessor("bufferInt")
    void setBufferInt(IntBuffer bufferInt);

    @Accessor("bufferFloat")
    FloatBuffer getBufferFloat();

    @Accessor("bufferFloat")
    void setBufferFloat(FloatBuffer bufferFloat);

    @Accessor("bufferShort")
    ShortBuffer getBufferShort();

    @Accessor("bufferShort")
    void setBufferShort(ShortBuffer bufferShort);

    @Accessor("array")
    int[] getArray();

    @Accessor("array")
    void setArray(int[] array);

    @Accessor("count")
    int getCount();

    @Accessor("count")
    void setCount(int count);

    @Accessor("u")
    double getU();

    @Accessor("u")
    void setU(double u);

    @Accessor("v")
    double getV();

    @Accessor("v")
    void setV(double v);

    @Accessor("light")
    int getLight();

    @Accessor("light")
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

    @Accessor("hasLight")
    boolean hasLight();

    @Accessor("hasLight")
    void setHasLight(boolean hasLight);

    @Accessor("hasNormal")
    boolean hasNormal();

    @Accessor("hasNormal")
    void setHasNormal(boolean hasNormal);

    @Accessor("arrayIdx")
    int getArrayIdx();

    @Accessor("arrayIdx")
    void setArrayIdx(int arrayIdx);

    @Accessor("vertexCount")
    int getVertexCount();

    @Accessor("vertexCount")
    void setVertexCount(int vertexCount);

    @Accessor("fixedColor")
    boolean getFixedColor();

    @Accessor("fixedColor")
    void setFixedColor(boolean fixedColor);

    @Accessor("format")
    int getFormat();

    @Accessor("format")
    void setFormat(int format);

    @Accessor("offsetX")
    double getOffsetX();

    @Accessor("offsetX")
    void setOffsetX(double offsetX);

    @Accessor("offsetY")
    double getOffsetY();

    @Accessor("offsetY")
    void setOffsetY(double offsetY);

    @Accessor("offsetZ")
    double getOffsetZ();

    @Accessor("offsetZ")
    void setOffsetZ(double offsetZ);

    @Accessor("normal")
    int getNormal();

    @Accessor("normal")
    void setNormal(int normal);

    @Accessor("tessellating")
    boolean isTessellating();

    @Accessor("tessellating")
    void setTessellating(boolean tessellating);

    @Accessor("field_1940")
    boolean getField1940();

    @Accessor("field_1940")
    void setField1940(boolean field1940);

    @Accessor("vboBuffer")
    IntBuffer getVboBuffer();

    @Accessor("vboBuffer")
    void setVboBuffer(IntBuffer vboBuffer);

    @Accessor("field_1942")
    int getField1942();

    @Accessor("field_1942")
    void setField1942(int field1942);

    @Accessor("field_1943")
    int getField1943();

    @Accessor("field_1943")
    void setField1943(int field1943);

    @Accessor("bufferCapacity")
    int getBufferCapacity();

    @Accessor("bufferCapacity")
    void setBufferCapacity(int bufferCapacity);

    @Invoker("reset")
    void callReset();
}
