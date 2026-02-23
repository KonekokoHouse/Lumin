package com.github.lumin.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public interface IComponent {
    default void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float deltaTicks) {
    }

    default boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        return false;
    }

    default boolean mouseReleased(MouseButtonEvent click) {
        return false;
    }

    default boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return false;
    }

    default boolean keyPressed(KeyEvent input) {
        return false;
    }

    default boolean charTyped(CharacterEvent input) {
        return false;
    }
}
