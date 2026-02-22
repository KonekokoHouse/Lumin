package com.github.lumin.graphics.renderers;

import com.github.lumin.graphics.LuminRenderSystem;
import com.github.lumin.graphics.text.ITextRenderer;
import com.github.lumin.graphics.text.ttf.TtfTextRenderer;

import java.awt.*;

public class TextRenderer implements IRenderer {

    private final ITextRenderer textRenderer = new TtfTextRenderer();

    public void addText(String text, float x, float y, Color color, float scale) {
        textRenderer.addText(text, x, y, color, scale);
    }

    @Override
    public void draw() {
        LuminRenderSystem.applyOrthoProjection();

        textRenderer.draw();
    }

    @Override
    public void clear() {
        textRenderer.clear();
    }
}
