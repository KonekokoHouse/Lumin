package com.github.lumin.settings.impl;

import com.github.lumin.settings.AbstractSetting;

import java.awt.*;

public class ColorSetting extends AbstractSetting<Color> {
    public ColorSetting(String englishName, String chineseName, Color defaultValue, Dependency dependency) {
        super(englishName, chineseName, dependency);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public ColorSetting(String englishName, String chineseName, Color defaultValue) {
        this(englishName, chineseName, defaultValue, () -> true);
    }
}
