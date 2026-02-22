package com.github.lumin.graphics;

import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

import javax.annotation.Nonnull;

public record LuminTexture(
        @Nonnull GpuTexture texture,
        @Nonnull GpuTextureView textureView,
        @Nonnull GpuSampler sampler
) {

    public void close() {
        sampler.close();
        textureView.close();
        texture.close();
    }

}
