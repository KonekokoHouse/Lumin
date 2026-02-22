package com.github.lumin.graphics.text.ttf;

import com.github.lumin.utils.resources.ResourceLocationUtils;
import net.minecraft.resources.Identifier;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class TtfFontFile {

    private final ByteBuffer fontData;
    private final STBTTFontinfo fontInfo;

    private final int totalHeight;
    private final int padding;

    public TtfFontFile(Identifier ttfFile, int totalHeight, int padding) {
        fontData = ResourceLocationUtils.loadResource(ttfFile);

        fontInfo = STBTTFontinfo.create();

        if (!STBTruetype.stbtt_InitFont(fontInfo, fontData)) {
            MemoryUtil.memFree(fontData);
            throw new IllegalStateException("STB TrueType failed to load ttf font: " + ttfFile);
        }

        this.totalHeight = totalHeight;
        this.padding = padding;
    }

    public TtfGlyph generateGlyph(char ch) {
        final var scale = STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, totalHeight - padding * 2);
        final var glyphIndex = STBTruetype.stbtt_FindGlyphIndex(fontInfo, ch);

        byte onEdgeValue = (byte) 128;
        float pixelDistScale = (float) onEdgeValue / padding;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final var width = stack.callocInt(1);
            final var height = stack.callocInt(1);
            final var xOff = stack.callocInt(1);
            final var yOff = stack.callocInt(1);

            ByteBuffer sdfPixels = STBTruetype.stbtt_GetGlyphSDF(
                    fontInfo,
                    scale,
                    glyphIndex,
                    padding,
                    onEdgeValue,
                    pixelDistScale,
                    width,
                    height,
                    xOff, yOff
            );

            final var advance = stack.callocInt(1);
            final var lsb = stack.callocInt(1);
            STBTruetype.stbtt_GetGlyphHMetrics(fontInfo, glyphIndex, advance, lsb);

            return new TtfGlyph(sdfPixels, width.get(), height.get(), xOff.get(), yOff.get(), (int) (advance.get() * scale));
        }
    }

    public void destroy() {
        MemoryUtil.memFree(fontData);
    }

}
