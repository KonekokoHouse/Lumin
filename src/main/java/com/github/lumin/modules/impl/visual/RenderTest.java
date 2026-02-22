package com.github.lumin.modules.impl.visual;

import com.github.lumin.graphics.renderers.RectRenderer;
import com.github.lumin.graphics.renderers.RoundRectRenderer;
import com.github.lumin.graphics.renderers.TextRenderer;
import com.github.lumin.modules.Category;
import com.github.lumin.modules.Module;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class RenderTest extends Module {

    public static RenderTest INSTANCE = new RenderTest();

    private RenderTest() {
        super("RenderTest", "渲染测试", Category.VISUAL);
        keyBind = GLFW.GLFW_KEY_U;
    }

    private final Supplier<RectRenderer> rectRenderer = Suppliers.memoize(RectRenderer::new);
    private final Supplier<TextRenderer> textRenderer = Suppliers.memoize(TextRenderer::new);
    private final Supplier<RoundRectRenderer> roundRectRenderer = Suppliers.memoize(RoundRectRenderer::new);

    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Post event) {

        rectRenderer.get().addRect(10, 10, 200, 200, Color.WHITE);
        textRenderer.get().addText("Minecraft 原神 启动！", 10.0f, 10.0f, Color.BLACK, 1.0f);
        roundRectRenderer.get().addRoundRect(100, 100, 100, 100, 10.0f, Color.CYAN);

        rectRenderer.get().drawAndClear();
        roundRectRenderer.get().drawAndClear();
        textRenderer.get().drawAndClear();

    }

}
