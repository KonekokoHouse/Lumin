package com.github.lumin.settings.impl;

import com.github.lumin.settings.Setting;

public class ModeSetting extends Setting<String> {

    private final String[] modes;

    public ModeSetting(String chineseName, String defaultValue, String[] modes, Dependency dependency) {
        super(chineseName, dependency);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.modes = modes;
    }

    public ModeSetting(String chineseName, String defaultValue, String[] modes) {
        this(chineseName, defaultValue, modes, () -> true);
    }

    public boolean is(String string) {
        return this.getValue().equalsIgnoreCase(string);
    }

    public void setMode(String mode) {
        for (String e : modes) {
            if (e != null && e.equalsIgnoreCase(mode)) {
                this.setValue(e);
                return;
            }
        }
    }

    public String[] getModes() {
        return modes;
    }

    public int getModeIndex() {
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }
}