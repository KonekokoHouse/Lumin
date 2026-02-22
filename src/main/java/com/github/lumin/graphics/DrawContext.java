package com.github.lumin.graphics;

import com.github.lumin.graphics.renderers.*;
import com.mojang.blaze3d.systems.RenderPass;

import java.awt.*;

public class DrawContext implements AutoCloseable {

    private final RectRenderer rectRenderer = new RectRenderer();
    private final RoundRectRenderer roundRectRenderer = new RoundRectRenderer();
    private final LineRenderer lineRenderer = new LineRenderer();
    private final CircleRenderer circleRenderer = new CircleRenderer();
    private final GradientRectRenderer gradientRectRenderer = new GradientRectRenderer();
    private final TextRenderer textRenderer = new TextRenderer();
    private final BlurRenderer blurRenderer = new BlurRenderer();

    private RenderPass currentPass;
    private boolean passOpen = false;

    public void beginFrame() {
        LuminRenderSystem.applyOrthoProjection();
    }

    public void drawRect(float x, float y, float width, float height, Color color) {
        rectRenderer.addRect(x, y, width, height, color);
    }

    public void drawRoundRect(float x, float y, float width, float height, float radius, Color color) {
        roundRectRenderer.addRoundRect(x, y, width, height, radius, color);
    }

    public void drawLine(float x1, float y1, float x2, float y2, float lineWidth, Color color) {
        lineRenderer.addLine(x1, y1, x2, y2, lineWidth, color);
    }

    public void drawLine(float x1, float y1, float z1, float x2, float y2, float z2, Color color) {
        lineRenderer.addLine(x1, y1, z1, x2, y2, z2, color);
    }

    public void drawRectOutline(float x, float y, float width, float height, float lineWidth, Color color) {
        lineRenderer.addRectOutline(x, y, width, height, lineWidth, color);
    }

    public void drawRoundRectOutline(float x, float y, float width, float height, float radius, float lineWidth, Color color) {
        roundRectRenderer.addRoundRect(x - lineWidth, y - lineWidth, width + lineWidth * 2, height + lineWidth * 2, radius + lineWidth, color);
    }

    public void drawCircle(float centerX, float centerY, float radius, Color color) {
        circleRenderer.addFilledCircle(centerX, centerY, radius, color);
    }

    public void drawCircleOutline(float centerX, float centerY, float radius, float thickness, Color color) {
        circleRenderer.addCircle(centerX, centerY, radius, thickness, color);
    }

    public void drawRing(float centerX, float centerY, float innerRadius, float outerRadius, Color color) {
        circleRenderer.addRing(centerX, centerY, innerRadius, outerRadius, color);
    }

    public void drawGradientRect(float x, float y, float width, float height,
                                 Color color1, Color color2, GradientRectRenderer.GradientDirection direction) {
        gradientRectRenderer.addGradientRect(x, y, width, height, color1, color2, direction);
    }

    public void drawHorizontalGradient(float x, float y, float width, float height,
                                       Color leftColor, Color rightColor) {
        gradientRectRenderer.addHorizontalGradient(x, y, width, height, leftColor, rightColor);
    }

    public void drawVerticalGradient(float x, float y, float width, float height,
                                     Color topColor, Color bottomColor) {
        gradientRectRenderer.addVerticalGradient(x, y, width, height, topColor, bottomColor);
    }

    public void drawText(String text, float x, float y, Color color, float scale) {
        textRenderer.addText(text, x, y, color, scale);
    }

    public void drawTextWithShadow(String text, float x, float y, Color color, float scale) {
        Color shadowColor = new Color(0, 0, 0, color.getAlpha() / 2);
        textRenderer.addText(text, x + 1, y + 1, shadowColor, scale);
        textRenderer.addText(text, x, y, color, scale);
    }

    public void drawBlur(float x, float y, float width, float height, float radius, int passes) {
        blurRenderer.blurRegion(x, y, width, height, radius, passes);
    }

    public void pushScissor(int x, int y, int width, int height) {
        if (currentPass != null) {
            ScissorStack.push(currentPass, x, y, width, height);
        }
    }

    public void popScissor() {
        if (currentPass != null) {
            ScissorStack.pop(currentPass);
        }
    }

    public void flush() {
        rectRenderer.drawAndClear();
        roundRectRenderer.drawAndClear();
        lineRenderer.drawAndClear();
        circleRenderer.drawAndClear();
        gradientRectRenderer.drawAndClear();
        textRenderer.drawAndClear();
    }

    public void clear() {
        rectRenderer.clear();
        roundRectRenderer.clear();
        lineRenderer.clear();
        circleRenderer.clear();
        gradientRectRenderer.clear();
        textRenderer.clear();
    }

    @Override
    public void close() {
        flush();
        if (currentPass != null) {
            currentPass.close();
            currentPass = null;
            passOpen = false;
        }
        ScissorStack.clear(null);
    }

    public RectRenderer getRectRenderer() {
        return rectRenderer;
    }

    public RoundRectRenderer getRoundRectRenderer() {
        return roundRectRenderer;
    }

    public LineRenderer getLineRenderer() {
        return lineRenderer;
    }

    public CircleRenderer getCircleRenderer() {
        return circleRenderer;
    }

    public GradientRectRenderer getGradientRectRenderer() {
        return gradientRectRenderer;
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    public BlurRenderer getBlurRenderer() {
        return blurRenderer;
    }
}
