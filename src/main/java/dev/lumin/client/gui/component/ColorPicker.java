package dev.lumin.client.gui.component;

import dev.lumin.client.gui.animation.Animation;
import dev.lumin.client.gui.animation.AnimationUtil;
import dev.lumin.client.gui.animation.Easing;
import dev.lumin.client.gui.theme.Theme;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.PaintMode;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;
import net.minecraft.util.Mth;

import java.awt.*;
import java.util.function.Consumer;

public class ColorPicker extends Component {

    private int color;
    private final Consumer<Integer> onChanged;

    private boolean expanded = false;
    private float expandProgress = 0f;
    private Animation expandAnimation;

    private boolean draggingHue = false;
    private boolean draggingSB = false;

    private float hue = 0f;
    private float saturation = 1f;
    private float brightness = 1f;

    private static final float PREVIEW_SIZE = 24f;
    private static final float PICKER_WIDTH = 150f;
    private static final float PICKER_HEIGHT = 120f;
    private static final float HUE_BAR_HEIGHT = 12f;
    private static final float PADDING = 8f;

    public ColorPicker(int initialColor, Consumer<Integer> onChanged) {
        this.color = initialColor;
        this.onChanged = onChanged;
        this.width = PREVIEW_SIZE;
        this.height = PREVIEW_SIZE;

        float[] hsb = Color.RGBtoHSB(
                (initialColor >> 16) & 0xFF,
                (initialColor >> 8) & 0xFF,
                initialColor & 0xFF,
                null
        );
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }

    @Override
    public void render(Canvas canvas, float mouseX, float mouseY) {
        if (!visible) return;

        try (Paint paint = new Paint()) {
            paint.setColor(color);
            paint.setAntiAlias(true);

            canvas.drawRRect(
                    RRect.makeXYWH(x, y, PREVIEW_SIZE, PREVIEW_SIZE, Theme.Radius.SMALL),
                    paint
            );

            paint.setColor(Theme.BORDER);
            paint.setMode(PaintMode.STROKE);
            paint.setStrokeWidth(1f);
            canvas.drawRRect(
                    RRect.makeXYWH(x, y, PREVIEW_SIZE, PREVIEW_SIZE, Theme.Radius.SMALL),
                    paint
            );
        }

        if (expandProgress > 0) {
            float pickerX = x;
            float pickerY = y + PREVIEW_SIZE + PADDING;

            try (Paint paint = new Paint()) {
                paint.setColor(Theme.PANEL_BG);
                paint.setAntiAlias(true);

                canvas.drawRRect(
                        RRect.makeXYWH(pickerX - PADDING, pickerY - PADDING,
                                PICKER_WIDTH + PADDING * 2, PICKER_HEIGHT + HUE_BAR_HEIGHT + PADDING * 3,
                                Theme.Radius.MEDIUM),
                        paint
                );
            }

            renderSBPicker(canvas, pickerX, pickerY);
            renderHueBar(canvas, pickerX, pickerY + PICKER_HEIGHT + PADDING);
        }
    }

    private void renderSBPicker(Canvas canvas, float px, float py) {
        try (Paint paint = new Paint()) {
            paint.setAntiAlias(true);

            for (int i = 0; i < PICKER_WIDTH; i++) {
                for (int j = 0; j < PICKER_HEIGHT; j++) {
                    float s = (float) i / PICKER_WIDTH;
                    float b = 1f - (float) j / PICKER_HEIGHT;

                    int rgb = Color.HSBtoRGB(hue, s, b);
                    paint.setColor(rgb);
                    canvas.drawRect(Rect.makeXYWH(px + i, py + j, 1, 1), paint);
                }
            }
        }

        float thumbX = px + saturation * PICKER_WIDTH;
        float thumbY = py + (1 - brightness) * PICKER_HEIGHT;

        try (Paint paint = new Paint()) {
            paint.setColor(0xFFFFFFFF);
            paint.setAntiAlias(true);
            paint.setMode(PaintMode.STROKE);
            paint.setStrokeWidth(2f);

            canvas.drawCircle(thumbX, thumbY, 6f, paint);
        }
    }

    private void renderHueBar(Canvas canvas, float px, float py) {
        try (Paint paint = new Paint()) {
            paint.setAntiAlias(true);

            for (int i = 0; i < PICKER_WIDTH; i++) {
                float h = (float) i / PICKER_WIDTH;
                int rgb = Color.HSBtoRGB(h, 1f, 1f);
                paint.setColor(rgb);
                canvas.drawRect(Rect.makeXYWH(px + i, py, 1, HUE_BAR_HEIGHT), paint);
            }
        }

        float hueX = px + hue * PICKER_WIDTH;

        try (Paint paint = new Paint()) {
            paint.setColor(0xFFFFFFFF);
            paint.setAntiAlias(true);
            paint.setMode(PaintMode.STROKE);
            paint.setStrokeWidth(2f);

            canvas.drawRect(Rect.makeXYWH(hueX - 2, py - 2, 4, HUE_BAR_HEIGHT + 4), paint);
        }
    }

    @Override
    public void update() {
        super.update();

        if (expandAnimation != null) {
            expandAnimation.update();
            expandProgress = expandAnimation.getValue();
            if (expandAnimation.isFinished()) {
                expandAnimation = null;
            }
        }
    }

    @Override
    protected void onClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (!expanded) {
                expanded = true;
                expandAnimation = new Animation(expandProgress, 1f, AnimationUtil.DURATION_NORMAL, Easing.EASE_OUT);
                expandAnimation.start();
            } else {
                float pickerX = x;
                float pickerY = y + PREVIEW_SIZE + PADDING;

                if (mouseX >= pickerX && mouseX <= pickerX + PICKER_WIDTH &&
                        mouseY >= pickerY && mouseY <= pickerY + PICKER_HEIGHT) {
                    draggingSB = true;
                    updateSBFromMouse(mouseX, mouseY, pickerX, pickerY);
                } else if (mouseX >= pickerX && mouseX <= pickerX + PICKER_WIDTH &&
                        mouseY >= pickerY + PICKER_HEIGHT + PADDING &&
                        mouseY <= pickerY + PICKER_HEIGHT + PADDING + HUE_BAR_HEIGHT) {
                    draggingHue = true;
                    updateHueFromMouse(mouseX, pickerX);
                } else {
                    expanded = false;
                    expandAnimation = new Animation(expandProgress, 0f, AnimationUtil.DURATION_FAST, Easing.EASE_OUT);
                    expandAnimation.start();
                }
            }
        }
    }

    @Override
    protected void onRelease(double mouseX, double mouseY, int button) {
        draggingSB = false;
        draggingHue = false;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        float pickerX = x;
        float pickerY = y + PREVIEW_SIZE + PADDING;

        if (draggingSB) {
            updateSBFromMouse(mouseX, mouseY, pickerX, pickerY);
        } else if (draggingHue) {
            updateHueFromMouse(mouseX, pickerX);
        }
    }

    private void updateSBFromMouse(double mouseX, double mouseY, float pickerX, float pickerY) {
        saturation = Mth.clamp((float) (mouseX - pickerX) / PICKER_WIDTH, 0f, 1f);
        brightness = Mth.clamp(1f - (float) (mouseY - pickerY) / PICKER_HEIGHT, 0f, 1f);
        updateColor();
    }

    private void updateHueFromMouse(double mouseX, float pickerX) {
        hue = Mth.clamp((float) (mouseX - pickerX) / PICKER_WIDTH, 0f, 1f);
        updateColor();
    }

    private void updateColor() {
        color = Color.HSBtoRGB(hue, saturation, brightness);
        if (onChanged != null) {
            onChanged.accept(color);
        }
    }

    public void setColor(int color) {
        this.color = color;
        float[] hsb = Color.RGBtoHSB(
                (color >> 16) & 0xFF,
                (color >> 8) & 0xFF,
                color & 0xFF,
                null
        );
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }

    public int getColor() {
        return color;
    }

    public float getHue() {
        return hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getBrightness() {
        return brightness;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void collapse() {
        if (expanded) {
            expanded = false;
            expandAnimation = new Animation(expandProgress, 0f, AnimationUtil.DURATION_FAST, Easing.EASE_OUT);
            expandAnimation.start();
        }
    }
}
