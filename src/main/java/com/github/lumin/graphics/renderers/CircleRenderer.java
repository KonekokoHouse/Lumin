package com.github.lumin.graphics.renderers;

import com.github.lumin.graphics.LuminRenderPipelines;
import com.github.lumin.graphics.LuminRenderSystem;
import com.github.lumin.graphics.buffer.LuminBuffer;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.util.ARGB;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class CircleRenderer implements IRenderer {

    private static final long BUFFER_SIZE = 512 * 1024;
    private static final int STRIDE = 36;

    private final LuminBuffer buffer = new LuminBuffer(BUFFER_SIZE, GpuBuffer.USAGE_VERTEX);
    private long currentOffset = 0;
    private int vertexCount = 0;

    public void addCircle(float centerX, float centerY, float radius, float thickness, Color color) {
        int argb = ARGB.toABGR(color.getRGB());

        float expand = radius + thickness * 0.5f + 1.0f;

        float x1 = centerX - expand;
        float y1 = centerY - expand;
        float x2 = centerX + expand;
        float y2 = centerY + expand;

        addVertex(x1, y1, centerX, centerY, radius, thickness, argb);
        addVertex(x1, y2, centerX, centerY, radius, thickness, argb);
        addVertex(x2, y2, centerX, centerY, radius, thickness, argb);
        addVertex(x2, y1, centerX, centerY, radius, thickness, argb);
    }

    public void addFilledCircle(float centerX, float centerY, float radius, Color color) {
        addCircle(centerX, centerY, radius, radius * 2, color);
    }

    public void addRing(float centerX, float centerY, float innerRadius, float outerRadius, Color color) {
        float thickness = outerRadius - innerRadius;
        float radius = innerRadius + thickness * 0.5f;
        addCircle(centerX, centerY, radius, thickness, color);
    }

    public void addArc(float centerX, float centerY, float radius, float thickness,
                       float startAngle, float endAngle, int segments, Color color) {
        int argb = ARGB.toABGR(color.getRGB());
        float angleStep = (endAngle - startAngle) / segments;

        for (int i = 0; i < segments; i++) {
            float a1 = startAngle + i * angleStep;
            float a2 = a1 + angleStep;

            float expand = radius + thickness * 0.5f + 1.0f;

            float cos1 = (float) Math.cos(a1);
            float sin1 = (float) Math.sin(a1);
            float cos2 = (float) Math.cos(a2);
            float sin2 = (float) Math.sin(a2);

            float x1 = centerX + cos1 * expand;
            float y1 = centerY + sin1 * expand;
            float x2 = centerX + cos2 * expand;
            float y2 = centerY + sin2 * expand;
            float x3 = centerX - cos1 * expand;
            float y3 = centerY - sin1 * expand;
            float x4 = centerX - cos2 * expand;
            float y4 = centerY - sin2 * expand;
        }
    }

    private void addVertex(float vx, float vy, float cx, float cy, float radius, float thickness, int color) {
        long baseAddr = MemoryUtil.memAddress(buffer.getMappedBuffer());
        long p = baseAddr + currentOffset;

        MemoryUtil.memPutFloat(p, vx);
        MemoryUtil.memPutFloat(p + 4, vy);
        MemoryUtil.memPutFloat(p + 8, 0.0f);

        MemoryUtil.memPutInt(p + 12, color);

        MemoryUtil.memPutFloat(p + 16, cx);
        MemoryUtil.memPutFloat(p + 20, cy);

        MemoryUtil.memPutFloat(p + 24, radius);

        MemoryUtil.memPutFloat(p + 28, thickness);

        currentOffset += STRIDE;
        vertexCount++;
    }

    @Override
    public void draw() {
        if (vertexCount == 0) return;

        LuminRenderSystem.applyOrthoProjection();

        var target = net.minecraft.client.Minecraft.getInstance().getMainRenderTarget();
        if (target.getColorTextureView() == null) return;

        int indexCount = vertexCount / 4 * 6;

        RenderSystem.AutoStorageIndexBuffer autoIndices =
                RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        GpuBuffer ibo = autoIndices.getBuffer(indexCount);

        com.mojang.blaze3d.buffers.GpuBufferSlice dynamicUniforms = RenderSystem.getDynamicUniforms().writeTransform(
                RenderSystem.getModelViewMatrix(),
                new org.joml.Vector4f(1, 1, 1, 1),
                new org.joml.Vector3f(0, 0, 0),
                net.minecraft.client.renderer.rendertype.TextureTransform.DEFAULT_TEXTURING.getMatrix()
        );

        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                () -> "Circle Draw",
                target.getColorTextureView(), OptionalInt.empty(),
                target.getDepthTextureView(), OptionalDouble.empty())
        ) {
            pass.setPipeline(LuminRenderPipelines.CIRCLE);

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
