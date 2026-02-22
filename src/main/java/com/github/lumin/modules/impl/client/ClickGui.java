package com.github.lumin.modules.impl.client;

import com.github.lumin.modules.Category;
import com.github.lumin.modules.Module;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;

public class ClickGui extends Module {

    public static ClickGui INSTANCE = new ClickGui();

    public ClickGui() {
        super("ClickGui", "控制面板", Category.CLIENT);
        keyBind = GLFW_KEY_RIGHT_SHIFT;
    }

    @Override
    protected void onEnable() {
        if (nullCheck()) return;
    }

    @Override
    protected void onDisable() {
    }

}
