package dev.lumin.client.modules.impl.client;

import dev.lumin.client.gui.screen.ClickGuiScreen;
import dev.lumin.client.modules.Category;
import dev.lumin.client.modules.Module;
import net.minecraft.client.gui.screens.Screen;

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
        mc.setScreen(new ClickGuiScreen());
    }

    @Override
    protected void onDisable() {
        Screen currentScreen = mc.screen;
        if (currentScreen instanceof ClickGuiScreen) {
            mc.setScreen(null);
        }
    }

}
