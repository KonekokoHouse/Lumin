package com.github.lumin.settings;

import com.github.lumin.modules.impl.client.InterFace;

public class Setting<V> {

    protected final String englishName;
    protected final String chineseName;

    protected V value;
    protected V defaultValue;

    protected final Dependency dependency;

    public Setting(String englishName, String chineseName, Dependency dependency) {
        this.englishName = englishName;
        this.chineseName = chineseName;
        this.dependency = dependency;
    }

    public Setting(String englishName, String chineseName) {
        this(englishName, chineseName, () -> true);
    }

    public String getDisplayName() {
        return InterFace.isEnglish() ? englishName : chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getChineseName() {
        return chineseName;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public void reset() {
        this.value = this.defaultValue;
    }

    public V getDefaultValue() {
        return defaultValue;
    }

    public boolean isAvailable() {
        return dependency != null && this.dependency.check();
    }

    @FunctionalInterface
    public interface Dependency {
        boolean check();
    }

    public Dependency getDependency() {
        return dependency;
    }

}
