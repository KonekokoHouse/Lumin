package com.github.lumin.settings.impl;

import com.github.lumin.settings.AbstractSetting;

import java.awt.*;

public class ColorSetting extends AbstractSetting<Color> {
    private final boolean allowAlpha;

    public ColorSetting(String englishName, String chineseName, Color defaultValue) {
        this(englishName, chineseName, defaultValue, () -> true, true);
    }

    public ColorSetting(String englishName, String chineseName, Color defaultValue, boolean allowAlpha) {
        this(englishName, chineseName, defaultValue, () -> true, allowAlpha);
    }

    public ColorSetting(String englishName, String chineseName, Color defaultValue, Dependency dependency) {
        this(englishName, chineseName, defaultValue, () -> true, true);
    }

    public ColorSetting(String englishName, String chineseName, Color defaultValue, Dependency dependency, boolean allowAlpha) {
        super(englishName, chineseName, dependency);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.allowAlpha = allowAlpha;
    }

    public boolean isAllowAlpha() {
        return allowAlpha;
    }
}
