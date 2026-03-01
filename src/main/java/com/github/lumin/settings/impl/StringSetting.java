package com.github.lumin.settings.impl;

import com.github.lumin.settings.Setting;

public class StringSetting extends Setting<String> {

    public StringSetting(String chineseName, String defaultValue, Dependency dependency) {
        super(chineseName, dependency);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public StringSetting(String chineseName, String defaultValue) {
        this(chineseName, defaultValue, () -> true);
    }
}