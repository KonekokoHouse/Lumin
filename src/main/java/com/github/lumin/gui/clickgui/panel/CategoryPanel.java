package com.github.lumin.gui.clickgui.panel;

import com.github.lumin.gui.IComponent;
import com.github.lumin.graphics.renderers.RectRenderer;
import com.github.lumin.graphics.renderers.TextRenderer;
import com.github.lumin.graphics.renderers.TextureRenderer;
import com.github.lumin.modules.Module;
import com.github.lumin.modules.impl.client.InterFace;
import com.github.lumin.utils.resources.ResourceLocationUtils;
import net.minecraft.resources.Identifier;

import java.awt.*;
import java.util.List;

public class CategoryPanel implements IComponent {

    private static final float PANEL_WIDTH = 100.0f;
    private static final float HEADER_HEIGHT = 25.0f;
    private static final float ROW_HEIGHT = 15.0f;

    private static final Identifier PANEL_TEXTURE = ResourceLocationUtils.getIdentifier("jello/jellopanel.png");

    private final String categoryName;
    private final List<Module> modules;

    private float x;
    private float y;

    private boolean dragging;
    private float dragOffsetX;
    private float dragOffsetY;

    public CategoryPanel(float x, float y, String categoryName, List<Module> modules) {
        this.x = x;
        this.y = y;
        this.categoryName = categoryName;
        this.modules = modules;
    }

    public void render(int mouseX, int mouseY, TextureRenderer textureRenderer, RectRenderer rectRenderer, TextRenderer textRenderer) {
        if (dragging) {
            x = mouseX - dragOffsetX;
            y = mouseY - dragOffsetY;
        }

        textureRenderer.addTexture(PANEL_TEXTURE, x - 9.5f, y - 9.5f, 119.0f, 169.0f);

        float titleScale = 0.92f;
        float titleWidth = textRenderer.getWidth(categoryName, titleScale);
        float titleHeight = textRenderer.getHeight(titleScale);
        textRenderer.addText(categoryName, x + PANEL_WIDTH * 0.5f - titleWidth * 0.5f, y + HEADER_HEIGHT * 0.5f - titleHeight * 0.5f, Color.WHITE, titleScale);

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            float rowY = y + HEADER_HEIGHT + i * ROW_HEIGHT;

            boolean hovered = isHovering(mouseX, mouseY, x, rowY, PANEL_WIDTH, ROW_HEIGHT);
            if (module.isEnabled()) {
                rectRenderer.addRect(x, rowY, PANEL_WIDTH, ROW_HEIGHT, new Color(42, 165, 255, hovered ? 235 : 205));
            } else {
                rectRenderer.addRect(x, rowY, PANEL_WIDTH, ROW_HEIGHT, new Color(255, 255, 255, hovered ? 38 : 22));
            }

            String moduleName = InterFace.isEnglish() ? module.englishName : module.chineseName;
            float moduleScale = 0.78f;
            float moduleWidth = textRenderer.getWidth(moduleName, moduleScale);
            float moduleHeight = textRenderer.getHeight(moduleScale);
            Color moduleColor = module.isEnabled() ? Color.WHITE : new Color(18, 18, 18);
            textRenderer.addText(moduleName, x + PANEL_WIDTH * 0.5f - moduleWidth * 0.5f, rowY + ROW_HEIGHT * 0.5f - moduleHeight * 0.5f, moduleColor, moduleScale);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (isHovering(mouseX, mouseY, x, y, PANEL_WIDTH, HEADER_HEIGHT)) {
                dragging = true;
                dragOffsetX = (float) mouseX - x;
                dragOffsetY = (float) mouseY - y;
                return true;
            }

            for (int i = 0; i < modules.size(); i++) {
                float rowY = y + HEADER_HEIGHT + i * ROW_HEIGHT;
                if (isHovering(mouseX, mouseY, x, rowY, PANEL_WIDTH, ROW_HEIGHT)) {
                    modules.get(i).toggle();
                    return true;
                }
            }
        }

        return false;
    }

    public boolean mouseReleased(int button) {
        if (button == 0 && dragging) {
            dragging = false;
            return true;
        }
        return false;
    }

    public Module findModuleAt(double mouseX, double mouseY) {
        for (int i = 0; i < modules.size(); i++) {
            float rowY = y + HEADER_HEIGHT + i * ROW_HEIGHT;
            if (isHovering(mouseX, mouseY, x, rowY, PANEL_WIDTH, ROW_HEIGHT)) {
                return modules.get(i);
            }
        }
        return null;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return PANEL_WIDTH;
    }

    private static boolean isHovering(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
