package com.github.lumin.gui.clickgui.component;

import com.github.lumin.gui.Component;
import com.github.lumin.gui.IComponent;
import com.github.lumin.gui.clickgui.component.impl.*;
import com.github.lumin.modules.Module;
import com.github.lumin.settings.AbstractSetting;
import com.github.lumin.settings.impl.*;

import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleComponent implements IComponent {
    private final Module module;
    private final CopyOnWriteArrayList<Component> settings = new CopyOnWriteArrayList<>();

    public ModuleComponent(Module module) {
        this.module = module;

        for (AbstractSetting<?> setting : module.getSettings()) {
            if (setting instanceof BoolSetting boolValue) {
                settings.add(new BoolSettingComponent(boolValue));
            } else if (setting instanceof IntSetting intSetting) {
                settings.add(new NumberSettingComponent(intSetting));
            } else if (setting instanceof DoubleSetting doubleSetting) {
                settings.add(new NumberSettingComponent(doubleSetting));
            } else if (setting instanceof ModeSetting modeSetting) {
                settings.add(new ModeSettingComponent(modeSetting));
            } else if (setting instanceof ColorSetting colorSetting) {
                settings.add(new ColorSettingComponent(colorSetting));
            } else if (setting instanceof StringSetting stringSetting) {
                settings.add(new StringSettingComponent(stringSetting));
            }
        }
    }

    public Module getModule() {
        return module;
    }
}
