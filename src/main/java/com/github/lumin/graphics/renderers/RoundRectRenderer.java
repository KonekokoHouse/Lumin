package com.github.lumin.graphics.renderers;

import com.github.lumin.graphics.LuminRenderPipelines;
import com.github.lumin.graphics.LuminRenderSystem;
import com.github.lumin.graphics.buffer.LuminBuffer;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.ARGB;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class RoundRectRenderer implements IRenderer {

    private static final long BUFFER_SIZE = 2 * 1024 * 1024;
    private final LuminBuffer buffer = new LuminBuffer(BUFFER_SIZE, GpuBuffer.USAGE_VERTEX);
    private boolean flushBufferFlag = false;

    public void addRoundRect(float x, float y, float width, float height, float radius, Color color) {
        buffer.tryMap();
        flushBufferFlag = true;

        float x2 = x + width;
        float y2 = y + height;

        float expand = radius + 1.0f;
        float vx1 = x + expand;
        float vy1 = y + expand;
        float vx2 = x2 - expand;
        float vy2 = y2 - expand;

        int argb = color.getRGB();

        addVertex(x, y, vx1, vy1, vx2, vy2, radius, argb);
        addVertex(x, y2, vx1, vy1, vx2, y2, radius, argb);
        addVertex(x2, y2, vx1, vy1, vx2, vy2, radius, argb);
        addVertex(x2, y, vx1, vy1, vx2, vy2, radius, argb);
    }

    public void addRoundRectBloom(float x, float y, float width, float height, float radius, float glowRadius, Color color) {
        float baseAlpha = color.getAlpha() / 255.0f;
        int glowSteps = (int) Math.max(10, glowRadius * 2);

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        for (int i = 0; i < glowSteps; i++) {
            float progress = (float) i / glowSteps;
            float offset = glowRadius * progress;

            float alphaFactor = (float) Math.cos(progress * (Math.PI / 2));
            float currentAlpha = baseAlpha * alphaFactor * 0.3f;

            if (currentAlpha <= 0.005f) continue;

            int a = (int) (currentAlpha * 255.0f);
            Color glowColor = new Color(r, g, b, a);

            addRoundRect(x - offset, y - offset, width + offset * 2, height + offset * 2, radius + offset, glowColor);
        }
    }

    public void addRoundRectBloom(float x, float y, float width, float height, float radius, Color color) {
        addRoundRectBloom(x, y, width, height, radius, 7.0f, color);
    }

    private long currentOffset = 0;
    private int vertexCount = 0;

    private void addVertex(float vx, float vy, float x1, float y1, float x2, float y2, float radius, int color) {
        long baseAddr = MemoryUtil.memAddress(buffer.getMappedBuffer());
        long p = baseAddr + currentOffset;

        MemoryUtil.memPutFloat(p, vx);
        MemoryUtil.memPutFloat(p + 4, vy);
        MemoryUtil.memPutFloat(p + 8, 0.0f);

        MemoryUtil.memPutInt(p + 12, ARGB.toABGR(color));

        MemoryUtil.memPutFloat(p + 16, x1);
        MemoryUtil.memPutFloat(p + 20, y1);
        MemoryUtil.memPutFloat(p + 24, x2);
        MemoryUtil.memPutFloat(p + 28, y2);

        MemoryUtil.memPutFloat(p + 32, radius);

        currentOffset += 36;
        vertexCount++;
    }

    @Override
    public void draw() {
        LuminRenderSystem.QuadRenderingInfo info = LuminRenderSystem.prepareQuadRendering(vertexCount);
        if (info == null) return;
        if (info.target().getColorTextureView() == null) return;

        if (flushBufferFlag) {
            buffer.unmap();
        }
        flushBufferFlag = false;

        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                () -> "Round Rect Draw",
                info.target().getColorTextureView(), OptionalInt.empty(),
                info.target().getDepthTextureView(), OptionalDouble.empty())
        ) {
            pass.setPipeline(LuminRenderPipelines.ROUND_RECT);

            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", info.dynamicUniforms());
            pass.setVertexBuffer(0, buffer.getGpuBuffer());
            pass.setIndexBuffer(info.ibo(), info.autoIndices().type());

            pass.drawIndexed(0, 0, info.indexCount(), 1);
        }
    }

    @Override
    public void clear() {
        vertexCount = 0;
        currentOffset = 0;
        flushBufferFlag = false;
    }

    @Override
    public void close() {
        clear();
        buffer.close();
    }
}
