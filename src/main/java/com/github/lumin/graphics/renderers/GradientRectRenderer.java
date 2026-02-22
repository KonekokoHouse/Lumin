package com.github.lumin.graphics.renderers;

import com.github.lumin.graphics.LuminRenderPipelines;
import com.github.lumin.graphics.LuminRenderSystem;
import com.github.lumin.graphics.buffer.LuminBuffer;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ARGB;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class GradientRectRenderer implements IRenderer {

    private static final long BUFFER_SIZE = 512 * 1024;
    private static final int STRIDE = 36;

    private final LuminBuffer buffer = new LuminBuffer(BUFFER_SIZE, GpuBuffer.USAGE_VERTEX);
    private long currentOffset = 0;
    private int vertexCount = 0;

    public enum GradientDirection {
        HORIZONTAL,
        VERTICAL,
        DIAGONAL_DOWN,
        DIAGONAL_UP
    }

    public void addGradientRect(float x, float y, float width, float height, Color color1, Color color2, GradientDirection direction) {
        int argb1 = ARGB.toABGR(color1.getRGB());
        int argb2 = ARGB.toABGR(color2.getRGB());

        float startX, startY, endX, endY;
        switch (direction) {
            case VERTICAL -> {
                startX = x;
                startY = y;
                endX = x;
                endY = y + height;
            }
            case DIAGONAL_DOWN -> {
                startX = x;
                startY = y;
                endX = x + width;
                endY = y + height;
            }
            case DIAGONAL_UP -> {
                startX = x;
                startY = y + height;
                endX = x + width;
                endY = y;
            }
            default -> {
                startX = x;
                startY = y;
                endX = x + width;
                endY = y;
            }
        }

        addVertex(x, y, argb1, argb2, startX, startY, endX, endY);
        addVertex(x, y + height, argb1, argb2, startX, startY, endX, endY);
        addVertex(x + width, y + height, argb1, argb2, startX, startY, endX, endY);
        addVertex(x + width, y, argb1, argb2, startX, startY, endX, endY);
    }

    public void addHorizontalGradient(float x, float y, float width, float height, Color leftColor, Color rightColor) {
        addGradientRect(x, y, width, height, leftColor, rightColor, GradientDirection.HORIZONTAL);
    }

    public void addVerticalGradient(float x, float y, float width, float height, Color topColor, Color bottomColor) {
        addGradientRect(x, y, width, height, topColor, bottomColor, GradientDirection.VERTICAL);
    }

    private void addVertex(float x, float y, int color1, int color2, float startX, float startY, float endX, float endY) {
        long baseAddr = MemoryUtil.memAddress(buffer.getMappedBuffer());
        long p = baseAddr + currentOffset;

        MemoryUtil.memPutFloat(p, x);
        MemoryUtil.memPutFloat(p + 4, y);
        MemoryUtil.memPutFloat(p + 8, 0.0f);

        MemoryUtil.memPutInt(p + 12, color1);

        MemoryUtil.memPutInt(p + 16, color2);

        MemoryUtil.memPutFloat(p + 20, startX);
        MemoryUtil.memPutFloat(p + 24, startY);

        MemoryUtil.memPutFloat(p + 28, endX);
        MemoryUtil.memPutFloat(p + 32, endY);

        currentOffset += STRIDE;
        vertexCount++;
    }

    @Override
    public void draw() {
        if (vertexCount == 0) return;

        LuminRenderSystem.applyOrthoProjection();

        var target = Minecraft.getInstance().getMainRenderTarget();
        if (target.getColorTextureView() == null) return;

        int indexCount = vertexCount / 4 * 6;

        RenderSystem.AutoStorageIndexBuffer autoIndices =
                RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        GpuBuffer ibo = autoIndices.getBuffer(indexCount);

        GpuBufferSlice dynamicUniforms = RenderSystem.getDynamicUniforms().writeTransform(
                RenderSystem.getModelViewMatrix(),
                new Vector4f(1, 1, 1, 1),
                new Vector3f(0, 0, 0),
                net.minecraft.client.renderer.rendertype.TextureTransform.DEFAULT_TEXTURING.getMatrix()
        );

        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                () -> "Gradient Rect Draw",
                target.getColorTextureView(), OptionalInt.empty(),
                target.getDepthTextureView(), OptionalDouble.empty())
        ) {
            pass.setPipeline(LuminRenderPipelines.GRADIENT_RECT);

            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", dynamicUniforms);

            pass.setVertexBuffer(0, buffer.getGpuBuffer());
            pass.setIndexBuffer(ibo, autoIndices.type());
            pass.drawIndexed(0, 0, indexCount, 1);
        }
    }

    @Override
    public void clear() {
        vertexCount = 0;
        currentOffset = 0;
    }

}
