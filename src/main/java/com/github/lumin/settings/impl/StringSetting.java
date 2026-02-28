package com.github.lumin.settings.impl;

import com.github.lumin.settings.Setting;

public class StringSetting extends Setting<String> {

    public StringSetting(String englishName, String chineseName, String defaultValue, Dependency dependency) {
        super(englishName, chineseName, dependency);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public StringSetting(String englishName, String chineseName, String defaultValue) {
        this(englishName, chineseName, defaultValue, () -> true);
    }

}
