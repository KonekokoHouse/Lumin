package com.github.lumin.utils.rotation;

public enum MovementFix {
    OFF("Off"),
    NORMAL("Normal"),
    TRADITIONAL("Traditional"),
    BACKWARDS_SPRINT("Backwards Sprint");

    final String name;

    MovementFix(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
