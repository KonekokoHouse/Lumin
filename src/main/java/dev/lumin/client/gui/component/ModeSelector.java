package dev.lumin.client.gui.component;

import dev.lumin.client.graphics.skija.font.FontLoader;
import dev.lumin.client.gui.animation.Animation;
import dev.lumin.client.gui.animation.AnimationUtil;
import dev.lumin.client.gui.animation.Easing;
import dev.lumin.client.gui.theme.Theme;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.RRect;

import java.util.Arrays;
import java.util.function.Consumer;

public class ModeSelector extends Component {

    private String[] modes;
    private int selectedIndex;
    private final Consumer<String> onChanged;

    private Animation selectionAnimation;
    private float animatedX = 0f;
    private float animatedWidth = 0f;

    private static final float DEFAULT_HEIGHT = 24f;
    private static final float PADDING = 4f;
    private static final float ITEM_PADDING = 8f;

    public ModeSelector(String[] modes, String selected, Consumer<String> onChanged) {
        this.modes = modes;
        this.selectedIndex = Arrays.asList(modes).indexOf(selected);
        if (this.selectedIndex < 0) this.selectedIndex = 0;
        this.onChanged = onChanged;
        this.height = DEFAULT_HEIGHT;
    }

    @Override
    public void render(Canvas canvas, float mouseX, float mouseY) {
        if (!visible || modes == null || modes.length == 0) return;

        try (Paint paint = new Paint()) {
            paint.setColor(Theme.withAlpha(Theme.BORDER, 0.3f));
            paint.setAntiAlias(true);

            RRect bgRect = RRect.makeXYWH(x, y, width, height, Theme.Radius.SMALL);
            canvas.drawRRect(bgRect, paint);
        }

        if (selectedIndex >= 0 && selectedIndex < modes.length) {
            try (Paint paint = new Paint()) {
                paint.setColor(Theme.withAlpha(Theme.PRIMARY, 0.3f));
                paint.setAntiAlias(true);

                RRect selectRect = RRect.makeXYWH(
                        x + PADDING + animatedX,
                        y + PADDING,
                        animatedWidth,
                        height - PADDING * 2,
                        Theme.Radius.SMALL - PADDING / 2
                );
                canvas.drawRRect(selectRect, paint);
            }
        }

        try (Paint paint = new Paint()) {
            paint.setAntiAlias(true);

            float currentX = x + PADDING;
            for (int i = 0; i < modes.length; i++) {
                String mode = modes[i];
                float textWidth = measureText(mode);

                boolean isHovered = isMouseOverItem(mouseX, mouseY, currentX - x - PADDING, textWidth + ITEM_PADDING * 2);
                boolean isSelected = i == selectedIndex;

                paint.setColor(isSelected ? Theme.PRIMARY : (isHovered ? Theme.TEXT_DESCRIPTION : Theme.TEXT_DISABLED));

                float textX = currentX + ITEM_PADDING;
                float textY = y + height / 2f + 4f;

                canvas.drawString(mode, textX, textY, FontLoader.regular(12), paint);

                currentX += textWidth + ITEM_PADDING * 2;
            }
        }
    }

    private float measureText(String text) {
        return text.length() * 7f;
    }

    private boolean isMouseOverItem(double mouseX, double mouseY, float itemX, float itemWidth) {
        float itemAbsX = x + PADDING + itemX;
        return mouseX >= itemAbsX && mouseX <= itemAbsX + itemWidth &&
                mouseY >= y && mouseY <= y + height;
    }

    @Override
    public void update() {
        super.update();

        if (selectionAnimation != null) {
            selectionAnimation.update();
            animatedX = selectionAnimation.getValue();
            if (selectionAnimation.isFinished()) {
                selectionAnimation = null;
            }
        }

        recalculateWidth();
    }

    private void recalculateWidth() {
        float totalWidth = PADDING * 2;
        float[] itemWidths = new float[modes.length];

        for (int i = 0; i < modes.length; i++) {
            itemWidths[i] = measureText(modes[i]) + ITEM_PADDING * 2;
            totalWidth += itemWidths[i];
        }

        this.width = Math.max(totalWidth, 50f);

        if (selectedIndex >= 0 && selectedIndex < modes.length) {
            float targetX = 0;
            for (int i = 0; i < selectedIndex; i++) {
                targetX += itemWidths[i];
            }
            animatedWidth = itemWidths[selectedIndex];

            if (selectionAnimation == null) {
                animatedX = targetX;
            }
        }
    }

    @Override
    protected void onClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            float currentX = x + PADDING;

            for (int i = 0; i < modes.length; i++) {
                float itemWidth = measureText(modes[i]) + ITEM_PADDING * 2;

                if (mouseX >= currentX && mouseX <= currentX + itemWidth) {
                    setSelectedIndex(i);
                    break;
                }

                currentX += itemWidth;
            }
        }
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < modes.length && index != selectedIndex) {
            float oldX = animatedX;
            selectedIndex = index;

            float targetX = 0;
            for (int i = 0; i < selectedIndex; i++) {
                targetX += measureText(modes[i]) + ITEM_PADDING * 2;
            }

            selectionAnimation = new Animation(oldX, targetX, AnimationUtil.DURATION_FAST, Easing.EASE_OUT_CUBIC);
            selectionAnimation.start();

            if (onChanged != null) {
                onChanged.accept(modes[selectedIndex]);
            }
        }
    }

    public void setSelectedMode(String mode) {
        int index = Arrays.asList(modes).indexOf(mode);
        if (index >= 0) {
            setSelectedIndex(index);
        }
    }

    public String getSelectedMode() {
        return selectedIndex >= 0 && selectedIndex < modes.length ? modes[selectedIndex] : null;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public String[] getModes() {
        return modes;
    }

    public void setModes(String[] modes) {
        this.modes = modes;
        if (selectedIndex >= modes.length) {
            selectedIndex = modes.length - 1;
        }
        recalculateWidth();
    }
}
