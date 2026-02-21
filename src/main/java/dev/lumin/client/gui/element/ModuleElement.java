package dev.lumin.client.gui.element;

import dev.lumin.client.graphics.skija.util.SkijaHelper;
import dev.lumin.client.gui.animation.Animation;
import dev.lumin.client.gui.animation.AnimationUtil;
import dev.lumin.client.gui.animation.Easing;
import dev.lumin.client.gui.component.*;
import dev.lumin.client.gui.component.Component;
import dev.lumin.client.gui.theme.Theme;
import dev.lumin.client.modules.Module;
import dev.lumin.client.settings.AbstractSetting;
import dev.lumin.client.settings.impl.*;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.RRect;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleElement extends Component {

    private final Module module;
    private boolean expanded = false;
    private float expandProgress = 0f;
    private Animation expandAnimation;

    private final List<Component> settingComponents = new ArrayList<>();

    private static final float HEIGHT_COLLAPSED = 40f;
    private static final float HEIGHT_EXPANDED_BASE = 40f;
    private static final float SETTING_HEIGHT = 28f;
    private static final float SETTING_PADDING = 8f;
    private static final float INDICATOR_SIZE = 6f;
    private static final float INDICATOR_MARGIN = 12f;
    private static final float TEXT_MARGIN = 32f;

    public ModuleElement(Module module) {
        this.module = module;
        this.width = 280f;
        this.height = HEIGHT_COLLAPSED;
        createSettingComponents();
    }

    private void createSettingComponents() {
        settingComponents.clear();

        for (AbstractSetting<?> setting : module.settings) {
            if (setting.getEnglishName().equals("Hidden")) continue;
            if (!setting.isAvailable()) continue;

            Component component = createComponentForSetting(setting);
            if (component != null) {
                settingComponents.add(component);
            }
        }
    }

    private Component createComponentForSetting(AbstractSetting<?> setting) {
        if (setting instanceof BoolSetting boolSetting) {
            return new Toggle(boolSetting.getValue(), v -> boolSetting.setValue(v));
        } else if (setting instanceof DoubleSetting doubleSetting) {
            return new Slider(
                    doubleSetting.getValue(),
                    doubleSetting.getMin(),
                    doubleSetting.getMax(),
                    doubleSetting.getStep(),
                    v -> doubleSetting.setValue(v)
            );
        } else if (setting instanceof IntSetting intSetting) {
            return new Slider(
                    intSetting.getValue(),
                    intSetting.getMin(),
                    intSetting.getMax(),
                    intSetting.getStep(),
                    v -> intSetting.setValue(v.intValue())
            );
        } else if (setting instanceof ModeSetting modeSetting) {
            return new ModeSelector(
                    modeSetting.getModes(),
                    modeSetting.getValue(),
                    v -> modeSetting.setMode(v)
            );
        } else if (setting instanceof ColorSetting colorSetting) {
            Color c = colorSetting.getValue();
            int color = (c.getAlpha() << 24) | (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue();
            return new ColorPicker(color, v -> {
                Color newColor = new Color(
                        (v >> 16) & 0xFF,
                        (v >> 8) & 0xFF,
                        v & 0xFF,
                        (v >> 24) & 0xFF
                );
                colorSetting.setValue(newColor);
            });
        }
        return null;
    }

    @Override
    public void render(Canvas canvas, float mouseX, float mouseY) {
        if (!visible) return;

        renderBackground(canvas);
        renderIndicator(canvas);
        renderText(canvas);
        renderSettings(canvas, mouseX, mouseY);
    }

    private void renderBackground(Canvas canvas) {
        int bgColor = hovered
                ? Theme.withAlpha(Theme.HOVER_BG, 1f)
                : Theme.withAlpha(Theme.BACKGROUND, 0f);

        if (hovered || expanded) {
            SkijaHelper.drawRRect(x, y, width, getExpandedHeight(), Theme.Radius.MEDIUM, bgColor);
        }
    }

    private void renderIndicator(Canvas canvas) {
        float indicatorX = x + INDICATOR_MARGIN;
        float indicatorY = y + HEIGHT_COLLAPSED / 2f;

        if (module.isEnabled()) {
            SkijaHelper.drawCircle(indicatorX, indicatorY, INDICATOR_SIZE / 2f, Theme.PRIMARY);

            try (Paint glowPaint = SkijaHelper.createPaint(Theme.withAlpha(Theme.PRIMARY, 0.3f))) {
                glowPaint.setMaskFilter(io.github.humbleui.skija.MaskFilter.makeBlur(
                        io.github.humbleui.skija.FilterBlurMode.NORMAL, 4f));
                canvas.drawCircle(indicatorX, indicatorY, INDICATOR_SIZE / 2f + 2f, glowPaint);
            }
        } else {
            SkijaHelper.drawCircleStroke(indicatorX, indicatorY, INDICATOR_SIZE / 2f, Theme.BORDER, 1f);
        }
    }

    private void renderText(Canvas canvas) {
        Font titleFont = new Font(null, 14);
        Font descFont = new Font(null, 11);

        String name = module.chineseName != null ? module.chineseName : module.englishName;

        try (Paint paint = new Paint()) {
            paint.setAntiAlias(true);
            paint.setColor(module.isEnabled() ? Theme.TEXT_TITLE : Theme.TEXT_DESCRIPTION);

            canvas.drawString(name, x + TEXT_MARGIN, y + 16, titleFont, paint);
        }

        if (module.keyBind > 0) {
            try (Paint paint = new Paint()) {
                paint.setAntiAlias(true);
                paint.setColor(Theme.TEXT_DISABLED);

                String keyName = org.lwjgl.glfw.GLFW.glfwGetKeyName(module.keyBind, 0);
                if (keyName == null) {
                    keyName = "[" + module.keyBind + "]";
                } else {
                    keyName = "[" + keyName.toUpperCase() + "]";
                }

                float keyWidth = SkijaHelper.measureTextWidth(keyName, descFont);
                canvas.drawString(keyName, x + width - keyWidth - 12, y + 16, descFont, paint);
            }
        }
    }

    private void renderSettings(Canvas canvas, float mouseX, float mouseY) {
        if (expandProgress <= 0 || settingComponents.isEmpty()) return;

        float settingY = y + HEIGHT_EXPANDED_BASE;
        float settingX = x + TEXT_MARGIN;
        float settingWidth = width - TEXT_MARGIN * 2;

        canvas.save();
        RRect clipRect = RRect.makeXYWH(x, y + HEIGHT_EXPANDED_BASE - 4, width, getExpandedHeight() - HEIGHT_EXPANDED_BASE + 4, Theme.Radius.MEDIUM);
        canvas.clipRRect(clipRect, io.github.humbleui.skija.ClipMode.INTERSECT);

        for (int i = 0; i < settingComponents.size(); i++) {
            Component component = settingComponents.get(i);
            AbstractSetting<?> setting = module.settings.stream()
                    .filter(s -> !s.getEnglishName().equals("Hidden") && s.isAvailable())
                    .skip(i)
                    .findFirst()
                    .orElse(null);

            if (setting == null) continue;

            Font labelFont = new Font(null, 12);
            try (Paint paint = new Paint()) {
                paint.setAntiAlias(true);
                paint.setColor(Theme.TEXT_DESCRIPTION);
                canvas.drawString(setting.getChineseName() != null ? setting.getChineseName() : setting.getEnglishName(), settingX, settingY + 10, labelFont, paint);
            }

            component.setX(settingX + 100);
            component.setY(settingY);
            component.setWidth(settingWidth - 100);
            component.render(canvas, mouseX, mouseY);

            settingY += SETTING_HEIGHT;
        }

        canvas.restore();
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

        height = HEIGHT_COLLAPSED + (getExpandedHeight() - HEIGHT_COLLAPSED) * expandProgress;

        for (Component component : settingComponents) {
            component.update();
        }
    }

    private float getExpandedHeight() {
        if (settingComponents.isEmpty()) return HEIGHT_EXPANDED_BASE;
        return HEIGHT_EXPANDED_BASE + settingComponents.size() * SETTING_HEIGHT + SETTING_PADDING;
    }

    @Override
    protected void onClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (mouseY <= y + HEIGHT_COLLAPSED) {
                if (mouseX >= x + TEXT_MARGIN) {
                    module.toggle();
                } else {
                    toggleExpand();
                }
            } else if (expanded) {
                float settingY = y + HEIGHT_EXPANDED_BASE;
                for (Component component : settingComponents) {
                    if (mouseY >= settingY && mouseY <= settingY + SETTING_HEIGHT) {
                        component.mouseClicked(mouseX, mouseY, button);
                        break;
                    }
                    settingY += SETTING_HEIGHT;
                }
            }
        } else if (button == 1) {
            if (mouseY <= y + HEIGHT_COLLAPSED) {
                toggleExpand();
            }
        }
    }

    @Override
    protected void onRelease(double mouseX, double mouseY, int button) {
        for (Component component : settingComponents) {
            component.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (Component component : settingComponents) {
            component.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    public void toggleExpand() {
        expanded = !expanded;
        expandAnimation = new Animation(
                expandProgress,
                expanded ? 1f : 0f,
                AnimationUtil.DURATION_NORMAL,
                Easing.EASE_OUT_CUBIC
        );
        expandAnimation.start();
    }

    public void collapse() {
        if (expanded) {
            expanded = false;
            expandAnimation = new Animation(
                    expandProgress,
                    0f,
                    AnimationUtil.DURATION_FAST,
                    Easing.EASE_OUT
            );
            expandAnimation.start();
        }
    }

    public Module getModule() {
        return module;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public float getExpandProgress() {
        return expandProgress;
    }
}
