package com.github.lumin.gui.clickgui.component;

import com.github.lumin.gui.Component;
import com.github.lumin.gui.IComponent;
import com.github.lumin.gui.clickgui.component.impl.*;
import com.github.lumin.modules.Module;
import com.github.lumin.modules.impl.client.InterFace;
import com.github.lumin.settings.AbstractSetting;
import com.github.lumin.settings.impl.*;
import com.github.lumin.utils.render.MouseUtils;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleComponent implements IComponent {

    private final Module module;
    private float x, y, width, height;
    private final List<Component> settings = new CopyOnWriteArrayList<>();
    private String filterTextLower = "";

    public ModuleComponent(Module module) {
        this.module = module;

        for (AbstractSetting<?> setting : module.getSettings()) {
            if (setting instanceof BoolSetting boolValue) {
                settings.add(new BoolSettingComponent(boolValue));
            } else if (setting instanceof IntSetting intSetting) {
                settings.add(new IntSettingComponent(intSetting));
            } else if (setting instanceof DoubleSetting doubleSetting) {
                settings.add(new DoubleSettingComponent(doubleSetting));
            } else if (setting instanceof ModeSetting modeSetting) {
                settings.add(new ModeSettingComponent(modeSetting));
            } else if (setting instanceof ColorSetting colorSetting) {
                settings.add(new ColorSettingComponent(colorSetting));
            } else if (setting instanceof StringSetting stringSetting) {
                settings.add(new StringSettingComponent(stringSetting));
            }
        }
    }

    @Override
    public void render(RendererSet set, int mouseX, int mouseY, float partialTicks) {
        float guiScale = InterFace.INSTANCE.scale.getValue().floatValue();
        float padding = 8.0f * guiScale;
        float rowH = 18.0f * guiScale;
        float rowGap = 4.0f * guiScale;
        float radius = 8.0f * guiScale;

        set.bottomRoundRect().addRoundRect(x, y, width, height, radius, new java.awt.Color(25, 25, 25, 140));

        float titleScale = 1.15f * guiScale;
        set.font().addText(module.getName(), x + padding, y + padding - 1.5f * guiScale, titleScale, java.awt.Color.WHITE);

        float cursorY = y + padding + set.font().getHeight(titleScale) + 6.0f * guiScale;
        float itemX = x + padding;
        float itemW = Math.max(0.0f, width - padding * 2);

        for (Component setting : settings) {
            if (!isSettingVisible(setting)) continue;
            setting.setScale(guiScale);
            setting.setX(itemX);
            setting.setY(cursorY);
            setting.setWidth(itemW);
            setting.setHeight(rowH);
            setting.render(set, mouseX, mouseY, partialTicks);
            cursorY += rowH + rowGap;
        }

        for (Component setting : settings) {
            if (!isSettingVisible(setting)) continue;
            if (setting instanceof ColorSettingComponent c && c.isOpened()) {
                c.renderOverlay(set, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean focused) {
        boolean handled = false;
        for (Component setting : settings) {
            if (!isSettingVisible(setting)) continue;
            if (setting instanceof ColorSettingComponent c && c.isOpened()) {
                if (c.mouseClicked(event, focused)) {
                    return true;
                }
            }
        }
        if (isHovered((int) event.x(), (int) event.y())) {
            for (Component setting : settings) {
                if (!isSettingVisible(setting)) continue;
                if (setting.mouseClicked(event, focused)) {
                    handled = true;
                }
            }
        }
        return handled || IComponent.super.mouseClicked(event, focused);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        boolean handled = false;
        for (Component setting : settings) {
            if (!isSettingVisible(setting)) continue;
            if (setting instanceof ColorSettingComponent c && c.isOpened()) {
                if (c.mouseReleased(event)) {
                    return true;
                }
            }
        }
        if (isHovered((int) event.x(), (int) event.y())) {
            for (Component setting : settings) {
                if (!isSettingVisible(setting)) continue;
                if (setting.mouseReleased(event)) {
                    handled = true;
                }
            }
        }
        return handled || IComponent.super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        boolean handled = false;
        for (Component setting : settings) {
            if (!isSettingVisible(setting)) continue;
            if (setting instanceof ColorSettingComponent c && c.isOpened()) {
                if (c.keyPressed(event)) {
                    return true;
                }
            }
        }
        for (Component setting : settings) {
            if (!isSettingVisible(setting)) continue;
            if (setting.keyPressed(event)) {
                handled = true;
            }
        }
        return handled || IComponent.super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        boolean handled = false;
        for (Component setting : settings) {
            if (!isSettingVisible(setting)) continue;
            if (setting instanceof ColorSettingComponent c && c.isOpened()) {
                if (c.charTyped(input)) {
                    return true;
                }
            }
        }
        for (Component setting : settings) {
            if (!isSettingVisible(setting)) continue;
            if (setting.charTyped(input)) {
                handled = true;
            }
        }
        return handled || IComponent.super.charTyped(input);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Module getModule() {
        return module;
    }

    public List<Component> getSettings() {
        return settings;
    }

    public void setFilterText(String text) {
        if (text == null || text.isEmpty()) {
            filterTextLower = "";
            return;
        }
        filterTextLower = text.toLowerCase();
    }

    public int getFilteredVisibleCount() {
        int count = 0;
        for (Component setting : settings) {
            if (isSettingVisible(setting)) count++;
        }
        return count;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return MouseUtils.isHovering(x, y, width, height, mouseX, mouseY);
    }

    private boolean isSettingVisible(Component component) {
        if (!component.isVisible()) return false;
        if (!filterTextLower.isEmpty()) {
            String name = getSettingDisplayName(component);
            if (!name.toLowerCase().startsWith(filterTextLower.toLowerCase())) return false;
        }
        return isSettingAvailable(component);
    }

    private boolean isSettingAvailable(Component component) {
        if (component instanceof BoolSettingComponent c) return c.getSetting().isAvailable();
        if (component instanceof IntSettingComponent c) return c.getSetting().isAvailable();
        if (component instanceof DoubleSettingComponent c) return c.getSetting().isAvailable();
        if (component instanceof ModeSettingComponent c) return c.getSetting().isAvailable();
        if (component instanceof ColorSettingComponent c) return c.getSetting().isAvailable();
        if (component instanceof StringSettingComponent c) return c.getSetting().isAvailable();
        return true;
    }

    private String getSettingDisplayName(Component component) {
        if (component instanceof BoolSettingComponent c) return c.getSetting().getDisplayName();
        if (component instanceof IntSettingComponent c) return c.getSetting().getDisplayName();
        if (component instanceof DoubleSettingComponent c) return c.getSetting().getDisplayName();
        if (component instanceof ModeSettingComponent c) return c.getSetting().getDisplayName();
        if (component instanceof ColorSettingComponent c) return c.getSetting().getDisplayName();
        if (component instanceof StringSettingComponent c) return c.getSetting().getDisplayName();
        return null;
    }

}
