package com.github.lumin.gui.clickgui;

import com.github.lumin.graphics.renderers.RectRenderer;
import com.github.lumin.graphics.renderers.TextRenderer;
import com.github.lumin.graphics.renderers.TextureRenderer;
import com.github.lumin.managers.Managers;
import com.github.lumin.gui.clickgui.panel.CategoryPanel;
import com.github.lumin.utils.resources.ResourceLocationUtils;
import com.github.lumin.settings.AbstractSetting;
import com.github.lumin.settings.impl.*;
import com.github.lumin.modules.Category;
import com.github.lumin.modules.Module;
import com.github.lumin.modules.impl.client.ClickGui;
import com.github.lumin.modules.impl.client.InterFace;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private final List<CategoryPanel> panels = new ArrayList<>();
    private final RectRenderer rectRenderer = new RectRenderer();
    private final TextureRenderer textureRenderer = new TextureRenderer(128 * 1024);
    private final TextRenderer textRenderer = new TextRenderer();

    private Module selectedModule;
    private CategoryPanel selectedPanel;
    private AbstractSetting<?> draggingSetting;
    private StringSetting editingStringSetting;

    private static final float POPUP_BASE_WIDTH = 100.0f;
    private static final float POPUP_BASE_HEIGHT = 150.0f;
    private static final float POPUP_EXTRA_WIDTH = 60.0f;
    private static final float POPUP_EXTRA_HEIGHT = 14.5f;
    private static final float POPUP_HEADER_HEIGHT = 25.0f;
    private static final float SETTING_ROW_HEIGHT = 17.0f;
    private static final float SETTING_PAD_TOP = 10.0f;
    private static final float SETTING_LABEL_X = 7.0f;

    private static final Identifier POPUP_OVERLAY = ResourceLocationUtils.getIdentifier("jello/JelloPanelOverlay.png");
    private static final Identifier CHECKED = ResourceLocationUtils.getIdentifier("jello/checked.png");
    private static final Identifier UNCHECKED = ResourceLocationUtils.getIdentifier("jello/unchecked.png");
    private static final Identifier SLIDER_HEAD = ResourceLocationUtils.getIdentifier("jello/sliderhead.png");

    public ClickGuiScreen() {
        super(Component.literal("ClickGui"));
    }

    @Override
    protected void init() {
        panels.clear();

        float panelX = 50.0f;
        float panelY = 50.0f;
        float panelGap = 24.0f;

        for (Category category : Category.values()) {
            List<Module> modules = Managers.MODULE.getModules().stream().filter(m -> m.category == category).toList();
            panels.add(new CategoryPanel(panelX, panelY, category.getName(), modules));
            panelX += 100.0f + panelGap;
        }
    }

    @Override
    public void removed() {
        rectRenderer.close();
        textureRenderer.close();
        textRenderer.close();
    }

    @Override
    public void render(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        rectRenderer.addRect(0, 0, width, height, new Color(0, 0, 0, 96));

        for (CategoryPanel panel : panels) {
            panel.render(mouseX, mouseY, textureRenderer, rectRenderer, textRenderer);
        }

        if (selectedModule != null && selectedPanel != null) {
            renderSettingsPopup(mouseX, mouseY);
        }

        rectRenderer.drawAndClear();
        textureRenderer.drawAndClear();
        textRenderer.drawAndClear();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean focused) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        if (selectedModule != null && selectedPanel != null && mouseClickedSettings(mouseX, mouseY, button)) {
            return true;
        }

        for (CategoryPanel panel : panels) {
            if (button == 1) {
                Module target = panel.findModuleAt(mouseX, mouseY);
                if (target != null) {
                    selectedModule = target;
                    selectedPanel = panel;
                    editingStringSetting = null;
                    return true;
                }
            }
            if (panel.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        return super.mouseClicked(event, focused);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        draggingSetting = null;

        for (CategoryPanel panel : panels) {
            if (panel.mouseReleased(event.button())) {
                return true;
            }
        }

        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (selectedModule != null && selectedPanel != null && editingStringSetting != null) {
            editingStringSetting = null;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (selectedModule != null && selectedPanel != null) {
            if (event.key() == 256) {
                selectedModule = null;
                selectedPanel = null;
                editingStringSetting = null;
                draggingSetting = null;
                return true;
            }

            if (editingStringSetting != null) {
                if (event.key() == 257 || event.key() == 335) {
                    editingStringSetting = null;
                    return true;
                }
                if (event.key() == 259) {
                    String v = editingStringSetting.getValue();
                    if (!v.isEmpty()) {
                        editingStringSetting.setValue(v.substring(0, v.length() - 1));
                    }
                    return true;
                }
            }
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (editingStringSetting != null) {
            if (event.isAllowedChatCharacter()) {
                editingStringSetting.setValue(editingStringSetting.getValue() + event.codepointAsString());
                return true;
            }
        }
        return super.charTyped(event);
    }

    @Override
    public void onClose() {
        if (ClickGui.INSTANCE.isEnabled()) {
            ClickGui.INSTANCE.setEnabled(false);
        } else {
            super.onClose();
        }
    }

    @Override
    public void renderBackground(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    private void renderSettingsPopup(int mouseX, int mouseY) {
        float popupLeft = Math.max(0.0f, selectedPanel.getX() - 30.0f);
        float popupTop = selectedPanel.getY();
        float popupWidth = POPUP_BASE_WIDTH + POPUP_EXTRA_WIDTH;
        float popupHeight = POPUP_BASE_HEIGHT + POPUP_EXTRA_HEIGHT;

        rectRenderer.addRect(0, 0, width, height, new Color(0, 0, 0, 96));

        textureRenderer.addTexture(POPUP_OVERLAY, popupLeft - 9.5f, popupTop - 9.5f, popupWidth + 19.0f, popupHeight + 19.0f);
        rectRenderer.addRect(popupLeft, popupTop, popupWidth, POPUP_HEADER_HEIGHT, new Color(153, 152, 154, 255));
        rectRenderer.addRect(popupLeft, popupTop + POPUP_HEADER_HEIGHT, popupWidth, popupHeight - POPUP_HEADER_HEIGHT, Color.WHITE);

        String panelName = selectedPanelTitle();
        float titleScale = 0.92f;
        float titleWidth = textRenderer.getWidth(panelName, titleScale);
        float titleHeight = textRenderer.getHeight(titleScale);
        textRenderer.addText(panelName, popupLeft + popupWidth * 0.5f - titleWidth * 0.5f, popupTop + POPUP_HEADER_HEIGHT * 0.5f - titleHeight * 0.5f, Color.WHITE, titleScale);

        List<AbstractSetting<?>> settings = selectedModule.getSettings().stream().filter(AbstractSetting::isAvailable).toList();
        for (int i = 0; i < settings.size(); i++) {
            AbstractSetting<?> setting = settings.get(i);
            float rowY = popupTop + POPUP_HEADER_HEIGHT + SETTING_PAD_TOP + i * SETTING_ROW_HEIGHT;
            renderSettingRow(setting, popupLeft, rowY, popupWidth, mouseX, mouseY);
        }
    }

    private void renderSettingRow(AbstractSetting<?> setting, float popupLeft, float rowY, float popupWidth, int mouseX, int mouseY) {
        String settingName = InterFace.isEnglish() ? setting.getEnglishName() : setting.getChineseName();
        float nameScale = 0.72f;
        textRenderer.addText(settingName, popupLeft + SETTING_LABEL_X, rowY + 2.0f, Color.BLACK, nameScale);

        if (setting instanceof BoolSetting boolSetting) {
            Identifier icon = boolSetting.getValue() ? CHECKED : UNCHECKED;
            textureRenderer.addTexture(icon, popupLeft + popupWidth - 19.0f, rowY, 12.0f, 12.0f);
            return;
        }

        if (setting instanceof IntSetting intSetting) {
            renderNumericSlider(popupLeft, rowY, popupWidth, intSetting.getMin(), intSetting.getMax(), intSetting.getValue().doubleValue(), intSetting.getDisplayName(), false);
            if (draggingSetting == intSetting) {
                intSetting.setValue((int) snapToStep(fromSliderValue(mouseX, popupLeft, popupWidth, intSetting.getMin(), intSetting.getMax()), intSetting.getStep()));
            }
            return;
        }

        if (setting instanceof DoubleSetting doubleSetting) {
            renderNumericSlider(popupLeft, rowY, popupWidth, doubleSetting.getMin(), doubleSetting.getMax(), doubleSetting.getValue(), String.format("%.2f", doubleSetting.getValue()), true);
            if (draggingSetting == doubleSetting) {
                double v = snapToStep(fromSliderValue(mouseX, popupLeft, popupWidth, doubleSetting.getMin(), doubleSetting.getMax()), doubleSetting.getStep());
                doubleSetting.setValue(v);
            }
            return;
        }

        if (setting instanceof ModeSetting modeSetting) {
            String mode = modeSetting.getValue();
            float scale = 0.68f;
            float modeWidth = textRenderer.getWidth(mode, scale);
            textRenderer.addText(mode, popupLeft + popupWidth - 10.0f - modeWidth, rowY + 2.0f, new Color(59, 153, 252), scale);
            return;
        }

        if (setting instanceof ColorSetting colorSetting) {
            rectRenderer.addRect(popupLeft + popupWidth - 22.0f, rowY + 1.0f, 14.0f, 10.0f, colorSetting.getValue());
            return;
        }

        if (setting instanceof StringSetting stringSetting) {
            String value = stringSetting.getValue();
            String display = editingStringSetting == stringSetting ? value + "_" : value;
            float scale = 0.68f;
            float textW = textRenderer.getWidth(display, scale);
            textRenderer.addText(display, popupLeft + popupWidth - 10.0f - textW, rowY + 2.0f, new Color(40, 40, 40), scale);
        }
    }

    private void renderNumericSlider(float popupLeft, float rowY, float popupWidth, double min, double max, double value, String valueText, boolean isDouble) {
        float sliderLeft = popupLeft + popupWidth - 63.0f;
        float sliderTop = rowY + 8.0f;
        float sliderWidth = 41.0f;

        double percent = (value - min) / (max - min);
        percent = Math.max(0.0, Math.min(1.0, percent));
        float fill = (float) (sliderWidth * percent);

        rectRenderer.addRect(sliderLeft, sliderTop, sliderWidth, 3.0f, new Color(0, 0, 0, 25));
        rectRenderer.addRect(sliderLeft, sliderTop, fill, 3.0f, new Color(59, 153, 252, 255));

        float headX = sliderLeft + fill - 5.75f;
        textureRenderer.addTexture(SLIDER_HEAD, headX, rowY - 1.5f, 11.5f, 12.5f);

        float valueScale = 0.62f;
        String text = isDouble ? valueText : String.valueOf((int) value);
        float textWidth = textRenderer.getWidth(text, valueScale);
        textRenderer.addText(text, sliderLeft + sliderWidth - textWidth, rowY - 5.5f, new Color(80, 80, 80), valueScale);
    }

    private boolean mouseClickedSettings(double mouseX, double mouseY, int button) {
        float popupLeft = Math.max(0.0f, selectedPanel.getX() - 30.0f);
        float popupTop = selectedPanel.getY();
        float popupWidth = POPUP_BASE_WIDTH + POPUP_EXTRA_WIDTH;
        float popupHeight = POPUP_BASE_HEIGHT + POPUP_EXTRA_HEIGHT;

        if (!hover(mouseX, mouseY, popupLeft, popupTop, popupWidth, popupHeight)) {
            if (button == 0 || button == 1) {
                selectedModule = null;
                selectedPanel = null;
                editingStringSetting = null;
                draggingSetting = null;
                return true;
            }
            return false;
        }

        if (button != 0 && button != 1) {
            return false;
        }

        List<AbstractSetting<?>> settings = selectedModule.getSettings().stream().filter(AbstractSetting::isAvailable).toList();
        for (int i = 0; i < settings.size(); i++) {
            AbstractSetting<?> setting = settings.get(i);
            float rowY = popupTop + POPUP_HEADER_HEIGHT + SETTING_PAD_TOP + i * SETTING_ROW_HEIGHT;

            if (setting instanceof BoolSetting boolSetting) {
                if (button == 0 && hover(mouseX, mouseY, popupLeft + popupWidth - 21.0f, rowY - 1.0f, 14.0f, 14.0f)) {
                    boolSetting.setValue(!boolSetting.getValue());
                    return true;
                }
            } else if (setting instanceof IntSetting intSetting) {
                float sliderLeft = popupLeft + popupWidth - 63.0f;
                if (hover(mouseX, mouseY, sliderLeft, rowY - 2.0f, 41.0f, 16.0f)) {
                    if (button == 0) {
                        draggingSetting = intSetting;
                        intSetting.setValue((int) snapToStep(fromSliderValue(mouseX, popupLeft, popupWidth, intSetting.getMin(), intSetting.getMax()), intSetting.getStep()));
                    }
                    return true;
                }
            } else if (setting instanceof DoubleSetting doubleSetting) {
                float sliderLeft = popupLeft + popupWidth - 63.0f;
                if (hover(mouseX, mouseY, sliderLeft, rowY - 2.0f, 41.0f, 16.0f)) {
                    if (button == 0) {
                        draggingSetting = doubleSetting;
                        doubleSetting.setValue(snapToStep(fromSliderValue(mouseX, popupLeft, popupWidth, doubleSetting.getMin(), doubleSetting.getMax()), doubleSetting.getStep()));
                    }
                    return true;
                }
            } else if (setting instanceof ModeSetting modeSetting) {
                if (hover(mouseX, mouseY, popupLeft + popupWidth - 70.0f, rowY - 2.0f, 62.0f, 14.0f)) {
                    cycleMode(modeSetting, button == 0);
                    return true;
                }
            } else if (setting instanceof ColorSetting colorSetting) {
                if (hover(mouseX, mouseY, popupLeft + popupWidth - 22.0f, rowY + 1.0f, 14.0f, 10.0f) && button == 0) {
                    colorSetting.setValue(nextColor(colorSetting.getValue()));
                    return true;
                }
            } else if (setting instanceof StringSetting stringSetting) {
                if (hover(mouseX, mouseY, popupLeft + popupWidth - 120.0f, rowY - 2.0f, 112.0f, 14.0f)) {
                    editingStringSetting = stringSetting;
                    return true;
                }
            }
        }

        return true;
    }

    private static boolean hover(double mouseX, double mouseY, float x, float y, float w, float h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    private static double fromSliderValue(double mouseX, float popupLeft, float popupWidth, double min, double max) {
        float sliderLeft = popupLeft + popupWidth - 63.0f;
        float sliderWidth = 41.0f;
        double percent = (mouseX - sliderLeft) / sliderWidth;
        percent = Math.max(0.0, Math.min(1.0, percent));
        return min + (max - min) * percent;
    }

    private static double snapToStep(double value, double step) {
        if (step <= 0.0) {
            return value;
        }
        return Math.round(value / step) * step;
    }

    private static void cycleMode(ModeSetting setting, boolean forward) {
        String[] modes = setting.getModes();
        if (modes.length == 0) {
            return;
        }
        int index = setting.getModeIndex();
        int next = forward ? (index + 1) % modes.length : (index - 1 + modes.length) % modes.length;
        setting.setMode(modes[next]);
    }

    private static Color nextColor(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        float hue = hsb[0] + 0.08f;
        if (hue > 1.0f) {
            hue -= 1.0f;
        }
        int rgb = Color.HSBtoRGB(hue, Math.max(0.45f, hsb[1]), Math.max(0.85f, hsb[2]));
        Color next = new Color(rgb);
        return new Color(next.getRed(), next.getGreen(), next.getBlue(), c.getAlpha());
    }

    private String selectedPanelTitle() {
        if (selectedModule == null) {
            return "Category";
        }
        Category category = selectedModule.category;
        return category.getName();
    }
}
