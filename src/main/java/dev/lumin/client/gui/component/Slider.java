package dev.lumin.client.gui.component;

import dev.lumin.client.gui.animation.Animation;
import dev.lumin.client.gui.animation.AnimationUtil;
import dev.lumin.client.gui.animation.Easing;
import dev.lumin.client.gui.theme.Theme;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.RRect;

import java.util.function.Consumer;

public class Slider extends Component {

    private double value;
    private final double min;
    private final double max;
    private final double step;
    private final Consumer<Double> onChanged;

    private boolean dragging = false;
    private Animation hoverAnimation;
    private float hoverProgress = 0f;

    private static final float TRACK_HEIGHT = 2f;
    private static final float THUMB_RADIUS = 6f;
    private static final float DEFAULT_WIDTH = 100f;
    private static final float DEFAULT_HEIGHT = 24f;

    public Slider(double value, double min, double max, double step, Consumer<Double> onChanged) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.step = step;
        this.onChanged = onChanged;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
    }

    public Slider(double value, double min, double max, Consumer<Double> onChanged) {
        this(value, min, max, 0.1, onChanged);
    }

    @Override
    public void render(Canvas canvas, float mouseX, float mouseY) {
        if (!visible) return;

        float trackY = y + (height - TRACK_HEIGHT) / 2f;
        float progress = (float) ((value - min) / (max - min));
        float thumbX = x + progress * (width - THUMB_RADIUS * 2) + THUMB_RADIUS;
        float thumbY = y + height / 2f;

        try (Paint paint = new Paint()) {
            paint.setColor(Theme.withAlpha(Theme.BORDER, 0.5f));
            paint.setAntiAlias(true);

            canvas.drawRRect(
                    RRect.makeXYWH(x, trackY, width, TRACK_HEIGHT, TRACK_HEIGHT / 2f),
                    paint
            );
        }

        if (progress > 0) {
            try (Paint paint = new Paint()) {
                int progressColor = Theme.interpolate(Theme.PRIMARY, Theme.SECONDARY, progress);
                paint.setColor(progressColor);
                paint.setAntiAlias(true);

                float progressWidth = Math.max(TRACK_HEIGHT, (thumbX - x));
                canvas.drawRRect(
                        RRect.makeXYWH(x, trackY, progressWidth, TRACK_HEIGHT, TRACK_HEIGHT / 2f),
                        paint
                );
            }
        }

        float thumbRadius = THUMB_RADIUS + (hovered || dragging ? 2f : 0f) * hoverProgress;

        try (Paint paint = new Paint()) {
            paint.setColor(0xFFFFFFFF);
            paint.setAntiAlias(true);

            canvas.drawCircle(thumbX, thumbY, thumbRadius, paint);
        }

        if (hovered || dragging) {
            try (Paint paint = new Paint()) {
                paint.setColor(Theme.withAlpha(Theme.PRIMARY, 0.3f * hoverProgress));
                paint.setAntiAlias(true);

                canvas.drawCircle(thumbX, thumbY, thumbRadius + 4f, paint);
            }
        }
    }

    @Override
    public void update() {
        super.update();

        if (hoverAnimation != null) {
            hoverAnimation.update();
            hoverProgress = hoverAnimation.getValue();
            if (hoverAnimation.isFinished()) {
                hoverAnimation = null;
            }
        }

        if (hovered && hoverAnimation == null && hoverProgress < 1f) {
            hoverAnimation = new Animation(hoverProgress, 1f, AnimationUtil.DURATION_FAST, Easing.EASE_OUT);
            hoverAnimation.start();
        } else if (!hovered && !dragging && hoverAnimation == null && hoverProgress > 0f) {
            hoverAnimation = new Animation(hoverProgress, 0f, AnimationUtil.DURATION_FAST, Easing.EASE_OUT);
            hoverAnimation.start();
        }
    }

    @Override
    protected void onClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = true;
            updateValueFromMouse(mouseX);
        }
    }

    @Override
    protected void onRelease(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = false;
        }
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            updateValueFromMouse(mouseX);
        }
    }

    private void updateValueFromMouse(double mouseX) {
        float relativeX = (float) (mouseX - x);
        float trackWidth = width - THUMB_RADIUS * 2;
        float progress = Math.max(0, Math.min(1, relativeX / trackWidth));

        double rawValue = min + progress * (max - min);

        if (step > 0) {
            rawValue = Math.round(rawValue / step) * step;
        }

        rawValue = Math.max(min, Math.min(max, rawValue));

        if (rawValue != value) {
            value = rawValue;
            if (onChanged != null) {
                onChanged.accept(value);
            }
        }
    }

    public void setValue(double value) {
        this.value = Math.max(min, Math.min(max, value));
    }

    public double getValue() {
        return value;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }

    public boolean isDragging() {
        return dragging;
    }
}
