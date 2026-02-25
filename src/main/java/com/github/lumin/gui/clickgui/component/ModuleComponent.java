package com.github.lumin.gui.clickgui.component;

import com.github.lumin.gui.Component;
import com.github.lumin.gui.IComponent;
import com.github.lumin.gui.clickgui.component.impl.*;
import com.github.lumin.modules.Module;
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

    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean focused) {
        boolean handled = false;
        if (!isHovered((int) event.x(), (int) event.y())) {
            for (Component setting : settings) {
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
        if (!isHovered((int) event.x(), (int) event.y())) {
            for (Component setting : settings) {
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

}
