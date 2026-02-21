package dev.lumin.client.gui.component;

import dev.lumin.client.gui.animation.Animation;
import dev.lumin.client.gui.animation.AnimationUtil;
import dev.lumin.client.gui.animation.Easing;
import dev.lumin.client.gui.theme.Theme;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.RRect;

import java.util.function.Consumer;

public class Toggle extends Component {

    private boolean value;
    private final Consumer<Boolean> onChanged;
    private Animation toggleAnimation;
    private float animationProgress = 0f;

    private static final float TRACK_WIDTH = 36f;
    private static final float TRACK_HEIGHT = 20f;
    private static final float THUMB_SIZE = 16f;
    private static final float THUMB_PADDING = 2f;

    public Toggle(boolean initialValue, Consumer<Boolean> onChanged) {
        this.value = initialValue;
        this.onChanged = onChanged;
        this.width = TRACK_WIDTH;
        this.height = TRACK_HEIGHT;
        this.animationProgress = value ? 1f : 0f;
    }

    @Override
    public void render(Canvas canvas, float mouseX, float mouseY) {
        if (!visible) return;

        float trackRadius = TRACK_HEIGHT / 2f;

        int trackColor = Theme.interpolate(
                Theme.withAlpha(Theme.BORDER, 0.8f),
                Theme.PRIMARY,
                animationProgress
        );

        try (Paint paint = new Paint()) {
            paint.setColor(trackColor);
            paint.setAntiAlias(true);

            RRect trackRect = RRect.makeXYWH(x, y, TRACK_WIDTH, TRACK_HEIGHT, trackRadius);
            canvas.drawRRect(trackRect, paint);
        }

        float thumbX = x + THUMB_PADDING + animationProgress * (TRACK_WIDTH - THUMB_SIZE - THUMB_PADDING * 2);
        float thumbY = y + THUMB_PADDING;

        int thumbColor = Theme.interpolate(
                0xFF888888,
                0xFFFFFFFF,
                animationProgress
        );

        try (Paint paint = new Paint()) {
            paint.setColor(thumbColor);
            paint.setAntiAlias(true);

            canvas.drawCircle(thumbX + THUMB_SIZE / 2f, thumbY + THUMB_SIZE / 2f, THUMB_SIZE / 2f, paint);
        }

        if (hovered) {
            try (Paint paint = new Paint()) {
                paint.setColor(Theme.HOVER_BG);
                paint.setAntiAlias(true);

                RRect hoverRect = RRect.makeXYWH(x, y, TRACK_WIDTH, TRACK_HEIGHT, trackRadius);
                canvas.drawRRect(hoverRect, paint);
            }
        }
    }

    @Override
    public void update() {
        super.update();
        if (toggleAnimation != null) {
            toggleAnimation.update();
            animationProgress = toggleAnimation.getValue();
            if (toggleAnimation.isFinished()) {
                toggleAnimation = null;
            }
        }
    }

    @Override
    protected void onClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            toggle();
        }
    }

    public void toggle() {
        value = !value;
        toggleAnimation = new Animation(
                animationProgress,
                value ? 1f : 0f,
                AnimationUtil.DURATION_FAST,
                Easing.EASE_OUT_CUBIC
        );
        toggleAnimation.start();

        if (onChanged != null) {
            onChanged.accept(value);
        }
    }

    public void setValue(boolean value, boolean animate) {
        if (this.value != value) {
            this.value = value;
            if (animate) {
                toggleAnimation = new Animation(
                        animationProgress,
                        value ? 1f : 0f,
                        AnimationUtil.DURATION_FAST,
                        Easing.EASE_OUT_CUBIC
                );
                toggleAnimation.start();
            } else {
                animationProgress = value ? 1f : 0f;
            }
        }
    }

    public boolean getValue() {
        return value;
    }

    public float getAnimationProgress() {
        return animationProgress;
    }
}
