package com.github.lumin.graphics.renderers;

import java.awt.*;

public class RoundRectRenderer implements IRenderer {

    private final RectRenderer rectRenderer = new RectRenderer();

    public void addRoundRect(float x, float y, float width, float height, float radius, Color color) {
        rectRenderer.addRect(x, y, width, height, color);
    }

    @Override
    public void draw() {
        rectRenderer.draw();
    }

    @Override
    public void clear() {
        rectRenderer.clear();
    }
}
