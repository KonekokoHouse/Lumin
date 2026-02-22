package com.github.lumin.graphics.buffer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;

import java.nio.ByteBuffer;

public class LuminBuffer {

    private final GpuBuffer gpuBuffer;

    private final GpuBuffer.MappedView mappedBuffer;

    public LuminBuffer(long size, @GpuBuffer.Usage int usage) {

        gpuBuffer = RenderSystem.getDevice().createBuffer(
                () -> "lumin-buffer",
                GpuBuffer.USAGE_MAP_WRITE | GpuBuffer.USAGE_COPY_DST | usage,
                size
        );

        mappedBuffer = RenderSystem.getDevice().createCommandEncoder().mapBuffer(
                gpuBuffer, false, true
        );

    }

    public ByteBuffer getMappedBuffer() {
        return mappedBuffer.data();
    }

    public void unmap() {
        mappedBuffer.close();
    }

    public GpuBuffer getGpuBuffer() {
        return gpuBuffer;
    }

    public void close() {
        gpuBuffer.close();
    }

}
