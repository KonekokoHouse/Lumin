package com.github.lumin.graphics.text.ttf;

import com.github.lumin.graphics.LuminTexture;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;

import java.util.OptionalDouble;

public class TtfGlyphAtlas {
    private static final int SIZE = 512;
    private final LuminTexture texture;

    private int currentX = 1;
    private int currentY = 1;
    private int currentRowHeight = 0;
    private static final int SPACING = 0;

    public TtfGlyphAtlas(int atlasId) {
        final var texture = RenderSystem.getDevice().createTexture(
                () -> "Lumin-TtfGlyphAtlas",
                GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_COPY_DST,
                TextureFormat.RED8,
                SIZE, SIZE,
                1, 1
        );

        final var textureView = RenderSystem.getDevice().createTextureView(texture);
        final var sampler = RenderSystem.getDevice().createSampler(
                AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE,
                FilterMode.LINEAR, FilterMode.LINEAR,
                1, OptionalDouble.empty()
        );

        this.texture = new LuminTexture(texture, textureView, sampler);
    }

    /**
     * Try to append a glyph to atlas
     * <p>
     * Return null if glyph atlas is full
     */
    public GlyphUV appendGlyph(TtfGlyph glyph) {
        if (glyph.glyphData() == null) return null;

        if (currentX + glyph.width() + SPACING >= SIZE) {
            currentX = 1;
            currentY += currentRowHeight + SPACING;
            currentRowHeight = 0;
        }

        // Return null if glyph atlas is full
        if (currentY + glyph.height() + SPACING >= SIZE) {
            return null;
        }

        RenderSystem.getDevice().createCommandEncoder().writeToTexture(
                this.texture.texture(),
                glyph.glyphData(),
                NativeImage.Format.LUMINANCE,
                0,
                0,
                currentX, currentY,
                glyph.width(),
                glyph.height()
        );

        int spacePixel = 1;

        GlyphUV uv = new GlyphUV(
                (float) (currentX + spacePixel) / SIZE,
                (float) (currentY + spacePixel) / SIZE,
                (float) (currentX + glyph.width() - 2 * spacePixel) / SIZE,
                (float) (currentY + glyph.height() - 2 * spacePixel) / SIZE
        );

        currentX += glyph.width() + SPACING;
        currentRowHeight = Math.max(currentRowHeight, glyph.height());

        return uv;
    }

    public LuminTexture getTexture() {
        return texture;
    }

    public void destroy() {
        texture.close();
    }

    public record GlyphUV(float u0, float v0, float u1, float v1) {
    }
}
