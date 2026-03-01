package com.github.lumin.settings.impl;

import com.github.lumin.settings.Setting;
import java.awt.*;

public class ColorSetting extends Setting<Color> {
    private final boolean allowAlpha;

    public ColorSetting(String chineseName, Color defaultValue) {
        this(chineseName, defaultValue, () -> true, true);
    }

    public ColorSetting(String chineseName, Color defaultValue, boolean allowAlpha) {
        this(chineseName, defaultValue, () -> true, allowAlpha);
    }

    public ColorSetting(String chineseName, Color defaultValue, Dependency dependency) {
        this(chineseName, defaultValue, dependency, true);
    }

    public ColorSetting(String chineseName, Color defaultValue, Dependency dependency, boolean allowAlpha) {
        super(chineseName, dependency);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.allowAlpha = allowAlpha;
    }

    public boolean isAllowAlpha() {
        return allowAlpha;
    }
}