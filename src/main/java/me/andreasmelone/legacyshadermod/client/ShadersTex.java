package me.andreasmelone.legacyshadermod.client;

import me.andreasmelone.legacyshadermod.mixin.AbstractTextureAccessor;
import me.andreasmelone.legacyshadermod.mixinif.IShaderTexture;
import me.andreasmelone.legacyshadermod.mixinif.ISpriteAtlasTexture;
import me.andreasmelone.legacyshadermod.transform.SMCLog;
import net.minecraft.src.AbstractTexture;
import net.minecraft.src.DynamicTexture;
import net.minecraft.src.LayeredTexture;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Resource;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.ResourceManager;
import net.minecraft.src.Stitcher;
import net.minecraft.src.TextureAtlasSprite;
import net.minecraft.src.TextureManager;
import net.minecraft.src.TextureMap;
import net.minecraft.src.TextureObject;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.andreasmelone.legacyshadermod.client.Shaders.checkGLError;
import static me.andreasmelone.legacyshadermod.client.Shaders.terrainIconSize;

public class ShadersTex {
    public static final int INITIAL_BUFFER_SIZE = 1048576;
    public static final int DEF_BASE_TEX_COLOR = 0;
    public static final int DEF_NORM_TEX_COLOR = 0xFF7F7FFF;
    public static final int DEF_SPEC_TEX_COLOR = 0;
    public static ByteBuffer byteBuffer = BufferUtils.createByteBuffer(4194304);
    public static IntBuffer intBuffer = byteBuffer.asIntBuffer();
    public static int[] intArray = new int[INITIAL_BUFFER_SIZE];
    public static Map<Integer, MultiTexID> multiTexMap = new HashMap<>();
    public static MultiTexID updatingTex = null;
    public static MultiTexID boundTex = null;
    public static int updatingPage = 0;
    static ResourceManager resManager = null;
    static ResourceLocation resLocation = null;
    static int imageSize = 0;

    public static IntBuffer getIntBuffer(int size) {
        if (intBuffer.capacity() < size) {
            int bufferSize = roundUpPOT(size);
            byteBuffer = BufferUtils.createByteBuffer(bufferSize * 4);
            intBuffer = byteBuffer.asIntBuffer();
        }

        return intBuffer;
    }

    public static int[] getIntArray(int size) {
        if (intArray.length < size) {
            intArray = null;
            intArray = new int[roundUpPOT(size)];
        }

        return intArray;
    }

    public static int roundUpPOT(int x) {
        int i = x - 1;
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        return i + 1;
    }

    public static IntBuffer fillIntBuffer(int size, int value) {
        int[] aint = getIntArray(size);
        IntBuffer intBuf = getIntBuffer(size);
        Arrays.fill(intArray, 0, size, value);
        intBuffer.put(intArray, 0, size);
        return intBuffer;
    }

    public static int[] createAIntImage(int size) {
        int[] aint = new int[size * 3];
        Arrays.fill(aint, 0, size, DEF_BASE_TEX_COLOR);
        Arrays.fill(aint, size, size * 2, DEF_NORM_TEX_COLOR);
        Arrays.fill(aint, size * 2, size * 3, DEF_SPEC_TEX_COLOR);
        return aint;
    }

    public static int[] createAIntImage(int size, int color) {
        int[] aint = new int[size * 3];
        Arrays.fill(aint, 0, size, color);
        Arrays.fill(aint, size, size * 2, DEF_NORM_TEX_COLOR);
        Arrays.fill(aint, size * 2, size * 3, DEF_SPEC_TEX_COLOR);
        return aint;
    }

    public static MultiTexID getMultiTexID(AbstractTexture tex) {
        MultiTexID multiTex = ((IShaderTexture) tex).shadermod$getInternallyStoredMultiTexId();
        if (multiTex == null) {
            int baseTex = tex.getGlTextureId();
            multiTex = multiTexMap.get(baseTex);
            if (multiTex == null) {
                multiTex = new MultiTexID(baseTex, GL11.glGenTextures(), GL11.glGenTextures());
                multiTexMap.put(baseTex, multiTex);
            }

            ((IShaderTexture) tex).shadermod$setMultiTexID(multiTex);
        }

        return multiTex;
    }

    public static void deleteTextures(AbstractTexture atex) {
        int texid = ((AbstractTextureAccessor) atex).shadermod$getGlId();
        GL11.glDeleteTextures(texid);
        ((AbstractTextureAccessor) atex).shadermod$setGlId(0);
        MultiTexID multiTex = ((IShaderTexture) atex).shadermod$getOriginalMultiTexId();
        if (multiTex != null) {
            ((IShaderTexture) atex).shadermod$setMultiTexID(null);
            multiTexMap.remove(multiTex.base);
            GL11.glDeleteTextures(multiTex.norm);
            GL11.glDeleteTextures(multiTex.spec);
            if (multiTex.base != texid) {
                System.err.println("Error : MultiTexID.base mismatch.");
                GL11.glDeleteTextures(multiTex.base);
            }
        }
    }

    public static int deleteMultiTex(TextureObject tex) {
        if (tex instanceof AbstractTexture) {
            deleteTextures((AbstractTexture) tex);
        } else {
            GL11.glDeleteTextures(tex.getGlTextureId());
        }

        return 0;
    }

    public static void bindTextures(int baseTex, int normTex, int specTex) {
        bindNSTextures(normTex, specTex);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, baseTex);
    }

    public static void bindNSTextures(int normTex, int specTex) {
        if (Shaders.isRenderingWorld && GLXActiveTexture.activeTexUnit == GL13.GL_TEXTURE0) {
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, normTex);
            GL13.glActiveTexture(GL13.GL_TEXTURE3);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, specTex);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
        }
    }

    public static void bindTextures(MultiTexID multiTex) {
        boundTex = multiTex;
        bindTextures(multiTex.base, multiTex.norm, multiTex.spec);
    }

    public static void bindTexture(TextureObject tex) {
        if (tex instanceof TextureMap) {
            Shaders.atlasSizeX = ((ISpriteAtlasTexture) tex).shadermod$getAtlasWidth();
            Shaders.atlasSizeY = ((ISpriteAtlasTexture) tex).shadermod$getAtlasHeight();
        } else {
            Shaders.atlasSizeX = 0;
            Shaders.atlasSizeY = 0;
        }

        bindTextures(((IShaderTexture) tex).shadermod$getMultiTexID());
    }

    public static void bindNSTextures(MultiTexID multiTex) {
        bindNSTextures(multiTex.norm, multiTex.spec);
    }

    public static void bindTextures(int baseTex) {
        MultiTexID multiTex = multiTexMap.get(baseTex);
        bindTextures(multiTex);
    }

    public static void allocTexStorage(int width, int height) {
        checkGLError("pre allocTexStorage");
        int level = 0;
        int wt = width;

        for (int ht = height; wt > 0 && ht > 0; level++) {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, level, GL11.GL_RGBA, wt, ht, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer) null);
            wt /= 2;
            ht /= 2;
        }

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, level - 1);
        checkGLError("allocTexStorage");
    }

    public static void initDynamicTexture(int texID, int width, int height, DynamicTexture tex) {
        MultiTexID multiTex = ((IShaderTexture) tex).shadermod$getMultiTexID();
        int[] aint = tex.getTextureData();
        int size = width * height;
        Arrays.fill(aint, size, size * 2, DEF_NORM_TEX_COLOR);
        Arrays.fill(aint, size * 2, size * 3, DEF_SPEC_TEX_COLOR);
        GL11.glDeleteTextures(multiTex.base);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.base);
        allocTexStorage(width, height);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glDeleteTextures(multiTex.norm);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.norm);
        allocTexStorage(width, height);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glDeleteTextures(multiTex.spec);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.spec);
        allocTexStorage(width, height);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.base);
    }

    public static TextureObject createDefaultTexture() {
        DynamicTexture tex = new DynamicTexture(1, 1);
        tex.getTextureData()[0] = -1;
        tex.updateDynamicTexture();
        return tex;
    }

    public static void setupTextureMap(int texID, int width, int height, Stitcher stitcher, TextureMap tex) {
        MultiTexID multiTex = getMultiTexID(tex);
        ((ISpriteAtlasTexture) tex).shadermod$setAtlasWidth(width);
        ((ISpriteAtlasTexture) tex).shadermod$setAtlasHeight(height);
        List<TextureAtlasSprite> spriteList = stitcher.getStichSlots();
        GL11.glDeleteTextures(multiTex.base);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.base);
        allocTexStorage(width, height);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.texMinFilValue[Shaders.configTexMinFilB]);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, Shaders.texMagFilValue[Shaders.configTexMagFilB]);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, 4);

        for (TextureAtlasSprite sprite : spriteList) {
            updateSubImage1(sprite.getFrameTextureData(0), sprite.getIconWidth(), sprite.getIconHeight(), sprite.getOriginX(), sprite.getOriginY(), 0, DEF_BASE_TEX_COLOR);
        }

        GL11.glDeleteTextures(multiTex.norm);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.norm);
        allocTexStorage(width, height);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.texMinFilValue[Shaders.configTexMinFilN]);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, Shaders.texMagFilValue[Shaders.configTexMagFilN]);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, 4);

        for (TextureAtlasSprite sprite : spriteList) {
            updateSubImage1(sprite.getFrameTextureData(0), sprite.getIconWidth(), sprite.getIconHeight(), sprite.getOriginX(), sprite.getOriginY(), 1, DEF_NORM_TEX_COLOR);
        }

        GL11.glDeleteTextures(multiTex.spec);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.spec);
        allocTexStorage(width, height);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.texMinFilValue[Shaders.configTexMinFilS]);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, Shaders.texMagFilValue[Shaders.configTexMagFilS]);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, 4);

        for (TextureAtlasSprite sprite : spriteList) {
            updateSubImage1(sprite.getFrameTextureData(0), sprite.getIconWidth(), sprite.getIconHeight(), sprite.getOriginX(), sprite.getOriginY(), 2, DEF_SPEC_TEX_COLOR);
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.base);
    }

    public static void updateTextureMap(int[] par0ArrayOfInteger, int par1, int par2, int par3, int par4, boolean par5, boolean par6) {
    }

    public static int blend4Alpha(int c0, int c1, int c2, int c3) {
        int a0 = c0 >>> 24 & 0xFF;
        int a1 = c1 >>> 24 & 0xFF;
        int a2 = c2 >>> 24 & 0xFF;
        int a3 = c3 >>> 24 & 0xFF;
        int as = a0 + a1 + a2 + a3;
        int an = (as + 2) / 4;
        int dv;
        if (as != 0) {
            dv = as;
        } else {
            dv = 4;
            a0 = 1;
            a1 = 1;
            a2 = 1;
            a3 = 1;
        }

        int frac = (dv + 1) / 2;
        return an << 24
                | ((c0 >>> 16 & 0xFF) * a0 + (c1 >>> 16 & 0xFF) * a1 + (c2 >>> 16 & 0xFF) * a2 + (c3 >>> 16 & 0xFF) * a3 + frac) / dv << 16
                | ((c0 >>> 8 & 0xFF) * a0 + (c1 >>> 8 & 0xFF) * a1 + (c2 >>> 8 & 0xFF) * a2 + (c3 >>> 8 & 0xFF) * a3 + frac) / dv << 8
                | ((c0 >>> 0 & 0xFF) * a0 + (c1 >>> 0 & 0xFF) * a1 + (c2 >>> 0 & 0xFF) * a2 + (c3 >>> 0 & 0xFF) * a3 + frac) / dv << 0;
    }

    public static int blend4Simple(int c0, int c1, int c2, int c3) {
        return ((c0 >>> 24 & 0xFF) + (c1 >>> 24 & 0xFF) + (c2 >>> 24 & 0xFF) + (c3 >>> 24 & 0xFF) + 2) / 4 << 24
                | ((c0 >>> 16 & 0xFF) + (c1 >>> 16 & 0xFF) + (c2 >>> 16 & 0xFF) + (c3 >>> 16 & 0xFF) + 2) / 4 << 16
                | ((c0 >>> 8 & 0xFF) + (c1 >>> 8 & 0xFF) + (c2 >>> 8 & 0xFF) + (c3 >>> 8 & 0xFF) + 2) / 4 << 8
                | ((c0 >>> 0 & 0xFF) + (c1 >>> 0 & 0xFF) + (c2 >>> 0 & 0xFF) + (c3 >>> 0 & 0xFF) + 2) / 4 << 0;
    }

    public static void genMipmapAlpha(int[] aint, int offset, int width, int height) {
        int minwh = Math.min(width, height);
        int o2 = offset;
        int w2 = width;
        int h2 = height;
        int o1 = 0;
        int w1 = 0;
        int h1 = 0;

        int level;
        for (level = 0; w2 > 1 && h2 > 1; o2 = o1) {
            o1 = o2 + w2 * h2;
            w1 = w2 / 2;
            h1 = h2 / 2;

            for (int y = 0; y < h1; y++) {
                int p1 = o1 + y * w1;
                int p2 = o2 + y * 2 * w2;

                for (int x = 0; x < w1; x++) {
                    aint[p1 + x] = blend4Alpha(aint[p2 + x * 2], aint[p2 + x * 2 + 1], aint[p2 + w2 + x * 2], aint[p2 + w2 + x * 2 + 1]);
                }
            }

            level++;
            w2 = w1;
            h2 = h1;
        }

        while (level > 0) {
            w2 = width >> --level;
            h2 = height >> level;
            o2 = o1 - w2 * h2;
            int p2 = o2;

            for (int y = 0; y < h2; y++) {
                for (int x = 0; x < w2; x++) {
                    if (aint[p2] == 0) {
                        aint[p2] = aint[o1 + y / 2 * w1 + x / 2] & 0xFFFFFF;
                    }

                    p2++;
                }
            }

            o1 = o2;
            w1 = w2;
        }
    }

    public static void genMipmapSimple(int[] aint, int offset, int width, int height) {
        int minwh = Math.min(width, height);
        int o2 = offset;
        int w2 = width;
        int h2 = height;
        int o1 = 0;
        int w1 = 0;
        int h1 = 0;

        int level;
        for (level = 0; w2 > 1 && h2 > 1; o2 = o1) {
            o1 = o2 + w2 * h2;
            w1 = w2 / 2;
            h1 = h2 / 2;

            for (int y = 0; y < h1; y++) {
                int p1 = o1 + y * w1;
                int p2 = o2 + y * 2 * w2;

                for (int x = 0; x < w1; x++) {
                    aint[p1 + x] = blend4Simple(aint[p2 + x * 2], aint[p2 + x * 2 + 1], aint[p2 + w2 + x * 2], aint[p2 + w2 + x * 2 + 1]);
                }
            }

            level++;
            w2 = w1;
            h2 = h1;
        }

        while (level > 0) {
            w2 = width >> --level;
            h2 = height >> level;
            o2 = o1 - w2 * h2;
            int p2 = o2;

            for (int y = 0; y < h2; y++) {
                for (int x = 0; x < w2; x++) {
                    if (aint[p2] == 0) {
                        aint[p2] = aint[o1 + y / 2 * w1 + x / 2] & 0xFFFFFF;
                    }

                    p2++;
                }
            }

            o1 = o2;
            w1 = w2;
        }
    }

    public static boolean isSemiTransparent(int[] aint, int width, int height) {
        int size = width * height;
        if (aint[0] >>> 24 == 0xFF && aint[size - 1] == 0) {
            return true;
        } else {
            for (int i = 0; i < size; i++) {
                int alpha = aint[i] >>> 24;
                if (alpha != 0 && alpha != 0xFF) {
                    return true;
                }
            }

            return false;
        }
    }

    public static void updateSubImage1(int[] src, int width, int height, int posX, int posY, int page, int color) {
        int size = width * height;
        IntBuffer intBuf = getIntBuffer(size);
        int[] aint = getIntArray((size * 4 + 2) / 3);
        if (src.length >= size * (page + 1)) {
            System.arraycopy(src, size * page, aint, 0, size);
        } else {
            Arrays.fill(aint, color);
        }

        genMipmapAlpha(aint, 0, width, height);
        int level = 0;
        int offset = 0;
        int lw = width;
        int lh = height;
        int px = posX;

        for (int py = posY; lw > 0 && lh > 0; level++) {
            int lsize = lw * lh;
            ((Buffer) intBuf).clear();
            ((Buffer) intBuf.put(aint, offset, lsize)).position(0).limit(lsize);
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, px, py, lw, lh, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuf);
            if(checkGLError("updateSubImage1", "glTexSubImage2D") != 0) {
                int boundTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                dumpTexture(boundTexture);
                try {
                    throw new Exception("Failed to glTexSubImage2D on " + boundTexture);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            offset += lsize;
            lw /= 2;
            lh /= 2;
            px /= 2;
            py /= 2;
        }

        ((Buffer) intBuf).clear();
    }

    public static void dumpTexture(int texture) {
        if (!GL11.glIsTexture(texture)) {
            System.out.println("Texture " + texture + " is not a valid texture.");
            return;
        }

//        int target = GL45.glGetTextureParameteri(texture, GL45.GL_TEXTURE_TARGET);

        System.out.println("======================================");
        System.out.println("Texture ID: " + texture);
//        System.out.println("Target: " + enumName(target));
        System.out.println();

        System.out.println("Filters");
        System.out.println("  Min Filter: " + enumName(GL45.glGetTextureParameteri(texture, GL11.GL_TEXTURE_MIN_FILTER)));
        System.out.println("  Mag Filter: " + enumName(GL45.glGetTextureParameteri(texture, GL11.GL_TEXTURE_MAG_FILTER)));

        System.out.println();

        System.out.println("Wrapping");
        System.out.println("  Wrap S: " + enumName(GL45.glGetTextureParameteri(texture, GL11.GL_TEXTURE_WRAP_S)));
        System.out.println("  Wrap T: " + enumName(GL45.glGetTextureParameteri(texture, GL11.GL_TEXTURE_WRAP_T)));
        System.out.println("  Wrap R: " + enumName(GL45.glGetTextureParameteri(texture, GL12.GL_TEXTURE_WRAP_R)));

        System.out.println();

        System.out.println("LOD");
        System.out.println("  Base Level: " + GL45.glGetTextureParameteri(texture, GL12.GL_TEXTURE_BASE_LEVEL));
        System.out.println("  Max Level : " + GL45.glGetTextureParameteri(texture, GL12.GL_TEXTURE_MAX_LEVEL));
        System.out.println("  Compare Mode: " + enumName(GL45.glGetTextureParameteri(texture, GL14.GL_TEXTURE_COMPARE_MODE)));
        System.out.println("  Compare Func: " + enumName(GL45.glGetTextureParameteri(texture, GL14.GL_TEXTURE_COMPARE_FUNC)));

        System.out.println();

        int levels = GL45.glGetTextureParameteri(texture, GL43.GL_TEXTURE_IMMUTABLE_LEVELS);
        boolean immutable = GL45.glGetTextureParameteri(texture, GL42.GL_TEXTURE_IMMUTABLE_FORMAT) != 0;

        System.out.println("Storage");
        System.out.println("  Immutable: " + immutable);
        System.out.println("  Levels   : " + levels);

        System.out.println();

        System.out.println("Mip Levels");
        for (int level = 0; level < Math.max(levels, 1); level++) {
            int width = GL45.glGetTextureLevelParameteri(texture, level, GL11.GL_TEXTURE_WIDTH);
            if (width == 0)
                break;

            int height = GL45.glGetTextureLevelParameteri(texture, level, GL11.GL_TEXTURE_HEIGHT);
            int depth = GL45.glGetTextureLevelParameteri(texture, level, GL12.GL_TEXTURE_DEPTH);
            int internalFormat = GL45.glGetTextureLevelParameteri(texture, level, GL11.GL_TEXTURE_INTERNAL_FORMAT);
            int red = GL45.glGetTextureLevelParameteri(texture, level, GL11.GL_TEXTURE_RED_SIZE);
            int green = GL45.glGetTextureLevelParameteri(texture, level, GL11.GL_TEXTURE_GREEN_SIZE);
            int blue = GL45.glGetTextureLevelParameteri(texture, level, GL11.GL_TEXTURE_BLUE_SIZE);
            int alpha = GL45.glGetTextureLevelParameteri(texture, level, GL11.GL_TEXTURE_ALPHA_SIZE);
            int depthBits = GL45.glGetTextureLevelParameteri(texture, level, GL14.GL_TEXTURE_DEPTH_SIZE);
            int stencilBits = GL45.glGetTextureLevelParameteri(texture, level, GL30.GL_TEXTURE_STENCIL_SIZE);

            System.out.printf(
                    "  Level %d%n" +
                            "    Size: %dx%dx%d%n" +
                            "    Internal Format: %s%n" +
                            "    Channels: R%d G%d B%d A%d D%d S%d%n",
                    level,
                    width, height, depth,
                    enumName(internalFormat),
                    red, green, blue, alpha, depthBits, stencilBits
            );
        }

        System.out.println("======================================");
    }

    private static String enumName(int value) {
        return switch (value) {
            case GL11.GL_TEXTURE_1D -> "GL_TEXTURE_1D";
            case GL11.GL_TEXTURE_2D -> "GL_TEXTURE_2D";
            case GL12.GL_TEXTURE_3D -> "GL_TEXTURE_3D";
            case GL30.GL_TEXTURE_1D_ARRAY -> "GL_TEXTURE_1D_ARRAY";
            case GL30.GL_TEXTURE_2D_ARRAY -> "GL_TEXTURE_2D_ARRAY";
            case GL31.GL_TEXTURE_RECTANGLE -> "GL_TEXTURE_RECTANGLE";
            case GL13.GL_TEXTURE_CUBE_MAP -> "GL_TEXTURE_CUBE_MAP";
            case GL40.GL_TEXTURE_CUBE_MAP_ARRAY -> "GL_TEXTURE_CUBE_MAP_ARRAY";

            case GL11.GL_LINEAR -> "GL_LINEAR";
            case GL11.GL_NEAREST -> "GL_NEAREST";
            case GL11.GL_LINEAR_MIPMAP_LINEAR -> "GL_LINEAR_MIPMAP_LINEAR";
            case GL11.GL_LINEAR_MIPMAP_NEAREST -> "GL_LINEAR_MIPMAP_NEAREST";
            case GL11.GL_NEAREST_MIPMAP_LINEAR -> "GL_NEAREST_MIPMAP_LINEAR";
            case GL11.GL_NEAREST_MIPMAP_NEAREST -> "GL_NEAREST_MIPMAP_NEAREST";

            case GL11.GL_REPEAT -> "GL_REPEAT";
            case GL12.GL_CLAMP_TO_EDGE -> "GL_CLAMP_TO_EDGE";
            case GL13.GL_CLAMP_TO_BORDER -> "GL_CLAMP_TO_BORDER";
            case GL14.GL_MIRRORED_REPEAT -> "GL_MIRRORED_REPEAT";

            case GL11.GL_RGBA8 -> "GL_RGBA8";
            case GL11.GL_RGB8 -> "GL_RGB8";
            case GL30.GL_RG8 -> "GL_RG8";
            case GL30.GL_R8 -> "GL_R8";
            case GL30.GL_RGBA16F -> "GL_RGBA16F";
            case GL30.GL_RGBA32F -> "GL_RGBA32F";
            case GL14.GL_DEPTH_COMPONENT24 -> "GL_DEPTH_COMPONENT24";
            case GL30.GL_DEPTH24_STENCIL8 -> "GL_DEPTH24_STENCIL8";
            case GL30.GL_DEPTH_COMPONENT32F -> "GL_DEPTH_COMPONENT32F";

            case GL11.GL_NONE -> "GL_NONE";
            case GL30.GL_COMPARE_REF_TO_TEXTURE -> "GL_COMPARE_REF_TO_TEXTURE";

            case GL11.GL_LEQUAL -> "GL_LEQUAL";
            case GL11.GL_GEQUAL -> "GL_GEQUAL";
            case GL11.GL_LESS -> "GL_LESS";
            case GL11.GL_GREATER -> "GL_GREATER";
            case GL11.GL_EQUAL -> "GL_EQUAL";
            case GL11.GL_NOTEQUAL -> "GL_NOTEQUAL";
            case GL11.GL_ALWAYS -> "GL_ALWAYS";
            case GL11.GL_NEVER -> "GL_NEVER";

            default -> String.format("0x%04X", value);
        };
    }

    public static void updateSubTex1(int[] src, int width, int height, int posX, int posY) {
        int level = 0;
        int cw = width;
        int ch = height;
        int cx = posX;

        for (int cy = posY; cw > 0 && ch > 0; cy /= 2) {
            GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, level, cx, cy, 0, 0, cw, ch);
            level++;
            cw /= 2;
            ch /= 2;
            cx /= 2;
        }
    }

    public static void setupTextureMipmap(TextureMap tex) {
    }

    public static void updateDynamicTexture(int texID, int[] src, int width, int height, DynamicTexture tex) {
        MultiTexID multiTex = ((IShaderTexture) tex).shadermod$getMultiTexID();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.norm);
        updateSubImage1(src, width, height, 0, 0, 1, DEF_NORM_TEX_COLOR);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.spec);
        updateSubImage1(src, width, height, 0, 0, 2, DEF_SPEC_TEX_COLOR);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.base);
        updateSubImage1(src, width, height, 0, 0, 0, DEF_BASE_TEX_COLOR);
    }

    public static void updateSubImage(int[] src, int width, int height, int posX, int posY, boolean linear, boolean clamp) {
        if (updatingTex != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, updatingTex.norm);
            updateSubImage1(src, width, height, posX, posY, 1, DEF_NORM_TEX_COLOR);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, updatingTex.spec);
            updateSubImage1(src, width, height, posX, posY, 2, DEF_SPEC_TEX_COLOR);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, updatingTex.base);
        }

        updateSubImage1(src, width, height, posX, posY, 0, DEF_BASE_TEX_COLOR);
    }

    public static void updateAnimationTextureMap(TextureMap tex, List<TextureAtlasSprite> tasList) {
        MultiTexID multiTex = ((IShaderTexture) tex).shadermod$getMultiTexID();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.norm);

        for (TextureAtlasSprite tas : tasList) {
            tas.updateAnimation();
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.norm);

        for (TextureAtlasSprite tas : tasList) {
            tas.updateAnimation();
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.norm);

        for (TextureAtlasSprite tas : tasList) {
            tas.updateAnimation();
        }
    }

    public static void setupTexture(MultiTexID multiTex, int[] src, int width, int height, boolean linear, boolean clamp) {
        int mmfilter = linear ? GL11.GL_LINEAR : GL11.GL_NEAREST;
        int wraptype = clamp ? GL11.GL_CLAMP : GL11.GL_REPEAT;
        int size = width * height;
        IntBuffer intBuf = getIntBuffer(size);
        ((Buffer) intBuf).clear();
        ((Buffer) intBuf.put(src, 0, size)).position(0).limit(size);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.base);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuf);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mmfilter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mmfilter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wraptype);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wraptype);
        ((Buffer) intBuf.put(src, size, size)).position(0).limit(size);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.norm);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuf);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mmfilter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mmfilter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wraptype);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wraptype);
        ((Buffer) intBuf.put(src, size * 2, size)).position(0).limit(size);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.spec);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuf);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mmfilter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mmfilter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wraptype);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wraptype);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.base);
    }

    public static void updateSubImage(MultiTexID multiTex, int[] src, int width, int height, int posX, int posY, boolean linear, boolean clamp) {
        int size = width * height;
        IntBuffer intBuf = getIntBuffer(size);
        ((Buffer) intBuf).clear();
        intBuf.put(src, 0, size);
        ((Buffer) intBuf).position(0).limit(size);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.base);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, posX, posY, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuf);
        if (src.length == size * 3) {
            ((Buffer) intBuf).clear();
            ((Buffer) intBuf.put(src, size, size)).position(0);
            ((Buffer) intBuf).position(0).limit(size);
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.norm);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, posX, posY, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuf);
        if (src.length == size * 3) {
            ((Buffer) intBuf).clear();
            intBuf.put(src, size * 2, size);
            ((Buffer) intBuf).position(0).limit(size);
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.spec);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, posX, posY, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuf);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
    }

    public static ResourceLocation getNSMapLocation(ResourceLocation location, String mapName) {
        String basename = location.getResourcePath();
        String[] basenameParts = basename.split(".png");
        String basenameNoFileType = basenameParts[0];
        return new ResourceLocation(location.getResourceDomain(), basenameNoFileType + "_" + mapName + ".png");
    }

    public static void loadNSMap(ResourceManager manager, ResourceLocation location, int width, int height, int[] aint) {
        loadNSMap1(manager, getNSMapLocation(location, "n"), width, height, aint, width * height, -8421377);
        loadNSMap1(manager, getNSMapLocation(location, "s"), width, height, aint, width * height * 2, 0);
    }

    public static void loadNSMap1(ResourceManager manager, ResourceLocation location, int width, int height, int[] aint, int offset, int defaultColor) {
        boolean good = false;

        try {
            Resource res = manager.getResource(location);
            BufferedImage bufferedimage = ImageIO.read(res.getInputStream());
            if (bufferedimage.getWidth() == width && bufferedimage.getHeight() == height) {
                bufferedimage.getRGB(0, 0, width, height, aint, offset, width);
                good = true;
            }
        } catch (IOException var10) {
        }

        if (!good) {
            Arrays.fill(aint, offset, offset + width * height, defaultColor);
        }
    }

    public static int loadSimpleTexture(
            int textureID, BufferedImage bufferedimage, boolean linear, boolean clamp, ResourceManager resourceManager, ResourceLocation location, MultiTexID multiTex
    ) {
        int width = bufferedimage.getWidth();
        int height = bufferedimage.getHeight();
        int size = width * height;
        int[] aint = getIntArray(size * 3);
        bufferedimage.getRGB(0, 0, width, height, aint, 0, width);
        loadNSMap(resourceManager, location, width, height, aint);
        setupTexture(multiTex, aint, width, height, linear, clamp);
        return textureID;
    }

    public static void mergeImage(int[] aint, int dstoff, int srcoff, int size) {
    }

    public static int blendColor(int color1, int color2, int factor1) {
        int factor2 = 0xFF - factor1;
        return ((color1 >>> 24 & 0xFF) * factor1 + (color2 >>> 24 & 0xFF) * factor2) / 0xFF << 24
                | ((color1 >>> 16 & 0xFF) * factor1 + (color2 >>> 16 & 0xFF) * factor2) / 0xFF << 16
                | ((color1 >>> 8 & 0xFF) * factor1 + (color2 >>> 8 & 0xFF) * factor2) / 0xFF << 8
                | ((color1 >>> 0 & 0xFF) * factor1 + (color2 >>> 0 & 0xFF) * factor2) / 0xFF << 0;
    }

    public static void loadLayeredTexture(LayeredTexture tex, ResourceManager manager, List<String> list) {
        int width = 0;
        int height = 0;
        int size = 0;
        int[] image = null;

        for (String s : list) {
            if (s != null) {
                try {
                    ResourceLocation location = new ResourceLocation(s);
                    InputStream inputstream = manager.getResource(location).getInputStream();
                    BufferedImage bufimg = ImageIO.read(inputstream);
                    if (size == 0) {
                        width = bufimg.getWidth();
                        height = bufimg.getHeight();
                        size = width * height;
                        image = createAIntImage(size, 0);
                    }

                    int[] aint = getIntArray(size * 3);
                    bufimg.getRGB(0, 0, width, height, aint, 0, width);
                    loadNSMap(manager, location, width, height, aint);

                    for (int i = 0; i < size; i++) {
                        int alpha = aint[i] >>> 24 & 0xFF;
                        image[size * 0 + i] = blendColor(aint[size * 0 + i], image[size * 0 + i], alpha);
                        image[size * 1 + i] = blendColor(aint[size * 1 + i], image[size * 1 + i], alpha);
                        image[size * 2 + i] = blendColor(aint[size * 2 + i], image[size * 2 + i], alpha);
                    }
                } catch (IOException var15) {
                    var15.printStackTrace();
                }
            }
        }

        setupTexture(((IShaderTexture) tex).shadermod$getMultiTexID(), image, width, height, false, false);
    }

    static void updateTextureMinMagFilter() {
        TextureManager texman = Minecraft.getMinecraft().getTextureManager();
        TextureObject texObj = texman.getTexture(TextureMap.locationBlocksTexture);
        if (texObj != null) {
            MultiTexID multiTex = ((IShaderTexture) texObj).shadermod$getMultiTexID();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.base);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.texMinFilValue[Shaders.configTexMinFilB]);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, Shaders.texMagFilValue[Shaders.configTexMagFilB]);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.norm);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.texMinFilValue[Shaders.configTexMinFilN]);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, Shaders.texMagFilValue[Shaders.configTexMagFilN]);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, multiTex.spec);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.texMinFilValue[Shaders.configTexMinFilS]);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, Shaders.texMagFilValue[Shaders.configTexMagFilS]);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        }
    }

    public static Resource loadResource(ResourceManager manager, ResourceLocation location) throws IOException {
        resManager = manager;
        resLocation = location;
        return manager.getResource(location);
    }

    public static int[] loadAtlasSprite(BufferedImage bufferedimage, int startX, int startY, int w, int h, int[] aint, int offset, int scansize) {
        imageSize = w * h;
        bufferedimage.getRGB(startX, startY, w, h, aint, offset, scansize);
        loadNSMap(resManager, resLocation, w, h, aint);
        return aint;
    }

    public static int[] extractFrame(int[] src, int width, int height, int frameIndex) {
        int srcSize = imageSize;
        int frameSize = width * height;
        int[] dst = new int[frameSize * 3];
        int srcPos = frameSize * frameIndex;
        int dstPos = 0;
        System.arraycopy(src, srcPos, dst, dstPos, frameSize);
        srcPos += srcSize;
        dstPos += frameSize;
        System.arraycopy(src, srcPos, dst, dstPos, frameSize);
        srcPos += srcSize;
        dstPos += frameSize;
        System.arraycopy(src, srcPos, dst, dstPos, frameSize);
        return dst;
    }

//   public static void uploadFrameTexture(Sprite tas, int frameIndex, int xPos, int yPos) {
//      int frameCount = tas.getSize();
//      if (frameIndex >= 0 && frameIndex < frameCount) {
//         if (frameCount <= 1) {
//            int[] buf = tas.getFrame(frameIndex);
//            IntBuffer data = getIntBuffer(tas.width * tas.height);
//            ((Buffer)data).clear();
//            data.put(buf, 0, tas.width * tas.height);
//            data.put(buf, 0, tas.width * tas.height);
//            ((Buffer)data).clear();
//            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, xPos, yPos, tas.width, tas.height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, data);
//         } else {
//            if (tas.frameBuffers == null) {
//               tas.frameBuffers = new IntBuffer[frameCount];
//
//               for (int var8 = 0; var8 < tas.frameBuffers.length; var8++) {
//                  int[] var10 = tas.getFrame(var8);
//                  IntBuffer buf1 = GlAllocationUtils.allocateIntBuffer(var10.length);
//                  buf1.put(var10);
//                  ((Buffer)buf1).clear();
//                  tas.frameBuffers[var8] = buf1;
//               }
//            }
//
//            IntBuffer var9 = tas.frameBuffers[frameIndex];
//            ((Buffer)var9).clear();
//            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, xPos, yPos, tas.width, tas.height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, var9);
//         }
//
//         if (tas.mipmapActive) {
//            tas.uploadFrameMipmaps(frameIndex, xPos, yPos);
//         }
//      }
//   }

    public static void fixTransparentColor(TextureAtlasSprite tas, int[] aint) {
    }
}
