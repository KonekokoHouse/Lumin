package com.github.lumin.settings.impl;

import com.github.lumin.settings.Setting;

public class BoolSetting extends Setting<Boolean> {

    public BoolSetting(String chineseName, boolean defaultValue, Dependency dependency) {
        super(chineseName, dependency);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public BoolSetting(String chineseName, boolean defaultValue) {
        this(chineseName, defaultValue, () -> true);
    }

}