package com.github.lumin.utils.rotation;

public enum MovementFix {
    OFF("Off"),
    ON("ON");

    final String name;

    MovementFix(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
