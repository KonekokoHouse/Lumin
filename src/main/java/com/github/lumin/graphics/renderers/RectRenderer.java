package com.github.lumin.graphics.renderers;

import com.github.lumin.graphics.LuminRenderSystem;
import com.github.lumin.graphics.LuminRenderTypes;
import com.mojang.blaze3d.vertex.*;

import java.awt.*;

public class RectRenderer implements IRenderer {

    private BufferBuilder bufferBuilder = Tesselator.getInstance()
            .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

    public void addRect(float x, float y, float width, float height, Color color) {
        bufferBuilder.addVertex(x, y, 0.0f).setColor(color.getRGB());
        bufferBuilder.addVertex(x + width, y, 0.0f).setColor(color.getRGB());
        bufferBuilder.addVertex(x + width, y + height, 0.0f).setColor(color.getRGB());
        bufferBuilder.addVertex(x, y + height, 0.0f).setColor(color.getRGB());
    }

    @Override
    public void draw() {
        LuminRenderSystem.applyOrthoProjection();

        MeshData meshData = bufferBuilder.build();
        if (meshData == null) return;

        LuminRenderTypes.RECTANGLE.draw(meshData);
    }

    @Override
    public void clear() {
        bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    }

}
