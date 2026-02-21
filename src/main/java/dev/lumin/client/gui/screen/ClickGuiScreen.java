package dev.lumin.client.gui.screen;

import dev.lumin.client.graphics.skija.Skija;
import dev.lumin.client.graphics.skija.util.SkijaHelper;
import dev.lumin.client.gui.animation.Animation;
import dev.lumin.client.gui.animation.AnimationUtil;
import dev.lumin.client.gui.animation.Easing;
import dev.lumin.client.gui.panel.CategoryPanel;
import dev.lumin.client.gui.panel.NavigationBar;
import dev.lumin.client.gui.theme.Theme;
import dev.lumin.client.modules.Category;
import dev.lumin.client.modules.Module;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickGuiScreen extends Screen {

    private final List<Module> modules;

    private float windowX = 100f;
    private float windowY = 50f;
    private static final float WINDOW_WIDTH = 420f;
    private static final float WINDOW_HEIGHT = 400f;
    private static final float TITLE_BAR_HEIGHT = 32f;
    private static final float NAV_WIDTH = 100f;
    private static final float CONTENT_PADDING = 8f;

    private NavigationBar navigationBar;
    private final Map<Category, CategoryPanel> categoryPanels = new HashMap<>();

    private boolean dragging = false;
    private double dragStartX = 0;
    private double dragStartY = 0;
    private float dragWindowX = 0;
    private float dragWindowY = 0;

    private Animation openAnimation;
    private float openProgress = 0f;

    public ClickGuiScreen(List<Module> modules) {
        super(Component.literal("ClickGUI"));
        this.modules = modules;
    }

    @Override
    protected void init() {
        super.init();

        navigationBar = new NavigationBar(this::onCategoryChanged);
        navigationBar.setX(windowX + CONTENT_PADDING);
        navigationBar.setY(windowY + TITLE_BAR_HEIGHT + CONTENT_PADDING);

        for (Category category : Category.values()) {
            CategoryPanel panel = new CategoryPanel(category);
            panel.setModules(modules);
            categoryPanels.put(category, panel);
        }

        updatePanelPositions();

        openProgress = 0f;
        openAnimation = new Animation(0f, 1f, AnimationUtil.DURATION_NORMAL, Easing.EASE_OUT_CUBIC);
        openAnimation.start();
    }

    private void onCategoryChanged(Category category) {
        CategoryPanel panel = categoryPanels.get(category);
        if (panel != null) {
            panel.setModules(modules);
        }
    }

    private void updatePanelPositions() {
        Category selectedCategory = navigationBar.getSelectedCategory();
        CategoryPanel activePanel = categoryPanels.get(selectedCategory);

        if (activePanel != null) {
            activePanel.setX(windowX + NAV_WIDTH + CONTENT_PADDING * 2);
            activePanel.setY(windowY + TITLE_BAR_HEIGHT + CONTENT_PADDING);
            activePanel.setWidth(WINDOW_WIDTH - NAV_WIDTH - CONTENT_PADDING * 3);
            activePanel.setHeight(WINDOW_HEIGHT - TITLE_BAR_HEIGHT - CONTENT_PADDING * 2);
        }
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (openAnimation != null) {
            openAnimation.update();
            openProgress = openAnimation.getValue();
            if (openAnimation.isFinished()) {
                openAnimation = null;
            }
        }

        Skija.draw(canvas -> renderGui(canvas, mouseX, mouseY));
    }

    private void renderGui(Canvas canvas, int mouseX, int mouseY) {
        canvas.save();

        float centerX = windowX + WINDOW_WIDTH / 2f;
        float centerY = windowY + WINDOW_HEIGHT / 2f;
        float scale = 0.95f + 0.05f * openProgress;
        float alpha = openProgress;

        canvas.translate(centerX, centerY);
        canvas.scale(scale, scale);
        canvas.translate(-centerX, -centerY);

        renderWindow(canvas, alpha);
        renderTitleBar(canvas, alpha);
        renderNavigationBar(canvas, mouseX, mouseY, alpha);
        renderCategoryPanel(canvas, mouseX, mouseY, alpha);

        canvas.restore();
    }

    private void renderWindow(Canvas canvas, float alpha) {
        int bgColor = Theme.withAlpha(Theme.PANEL_BG, alpha * 0.85f);
        SkijaHelper.drawRRect(windowX, windowY, WINDOW_WIDTH, WINDOW_HEIGHT, Theme.Radius.LARGE, bgColor);

        SkijaHelper.drawRRectStroke(windowX, windowY, WINDOW_WIDTH, WINDOW_HEIGHT, Theme.Radius.LARGE, Theme.withAlpha(Theme.BORDER, alpha * 0.3f), 1f);
    }

    private void renderTitleBar(Canvas canvas, float alpha) {
        Font titleFont = new Font(null, 16);
        Font versionFont = new Font(null, 11);

        try (Paint paint = new Paint()) {
            paint.setAntiAlias(true);
            paint.setColor(Theme.withAlpha(Theme.TEXT_TITLE, alpha));

            canvas.drawString("Lumin", windowX + 16, windowY + 20, titleFont, paint);
        }

        try (Paint paint = new Paint()) {
            paint.setAntiAlias(true);
            paint.setColor(Theme.withAlpha(Theme.TEXT_DISABLED, alpha));

            String version = "v1.0";
            float versionWidth = SkijaHelper.measureTextWidth(version, versionFont);
            canvas.drawString(version, windowX + WINDOW_WIDTH - versionWidth - 16, windowY + 20, versionFont, paint);
        }

        try (Paint paint = new Paint()) {
            paint.setAntiAlias(true);
            paint.setColor(Theme.withAlpha(Theme.BORDER, alpha * 0.3f));

            SkijaHelper.drawLine(windowX + 12, windowY + TITLE_BAR_HEIGHT - 1, windowX + WINDOW_WIDTH - 12, windowY + TITLE_BAR_HEIGHT - 1, paint);
        }
    }

    private void renderNavigationBar(Canvas canvas, int mouseX, int mouseY, float alpha) {
        if (navigationBar == null) return;

        navigationBar.setVisible(alpha > 0.5f);
        if (navigationBar.isVisible()) {
            navigationBar.updateHovered(mouseX, mouseY);
            navigationBar.render(canvas, mouseX, mouseY);
        }
    }

    private void renderCategoryPanel(Canvas canvas, int mouseX, int mouseY, float alpha) {
        Category selectedCategory = navigationBar.getSelectedCategory();
        CategoryPanel panel = categoryPanels.get(selectedCategory);

        if (panel == null) return;

        panel.setVisible(alpha > 0.5f);
        if (panel.isVisible()) {
            panel.updateHovered(mouseX, mouseY);
            panel.render(canvas, mouseX, mouseY);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (navigationBar != null) {
            navigationBar.update();
        }

        for (CategoryPanel panel : categoryPanels.values()) {
            panel.update();
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean transferFocus) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        if (openProgress < 0.5f) return false;

        if (isInTitleBar(mouseX, mouseY) && button == 0) {
            dragging = true;
            dragStartX = mouseX;
            dragStartY = mouseY;
            dragWindowX = windowX;
            dragWindowY = windowY;
            return true;
        }

        if (isInWindow(mouseX, mouseY)) {
            if (navigationBar != null && navigationBar.isMouseOver(mouseX, mouseY)) {
                navigationBar.mouseClicked(mouseX, mouseY, button);
                return true;
            }

            Category selectedCategory = navigationBar.getSelectedCategory();
            CategoryPanel panel = categoryPanels.get(selectedCategory);
            if (panel != null && panel.isMouseOver(mouseX, mouseY)) {
                panel.mouseClicked(mouseX, mouseY, button);
                return true;
            }
        }

        return super.mouseClicked(event, transferFocus);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        int button = event.button();

        if (button == 0) {
            dragging = false;
        }

        if (navigationBar != null) {
            navigationBar.mouseReleased(event.x(), event.y(), button);
        }

        for (CategoryPanel panel : categoryPanels.values()) {
            panel.mouseReleased(event.x(), event.y(), button);
        }

        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        if (dragging && button == 0) {
            windowX = (float) (dragWindowX + (mouseX - dragStartX));
            windowY = (float) (dragWindowY + (mouseY - dragStartY));

            windowX = Math.max(0, Math.min(this.width - WINDOW_WIDTH, windowX));
            windowY = Math.max(0, Math.min(this.height - WINDOW_HEIGHT, windowY));

            navigationBar.setX(windowX + CONTENT_PADDING);
            navigationBar.setY(windowY + TITLE_BAR_HEIGHT + CONTENT_PADDING);
            updatePanelPositions();

            return true;
        }

        for (CategoryPanel panel : categoryPanels.values()) {
            panel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (openProgress < 0.5f) return false;

        Category selectedCategory = navigationBar.getSelectedCategory();
        CategoryPanel panel = categoryPanels.get(selectedCategory);

        if (panel != null && panel.isMouseOver(mouseX, mouseY)) {
            panel.mouseScrolled(mouseX, mouseY, scrollY);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }

        for (CategoryPanel panel : categoryPanels.values()) {
            if (panel.keyPressed(event.key(), event.scancode(), event.modifiers())) {
                return true;
            }
        }

        return super.keyPressed(event);
    }

    private boolean isInTitleBar(double mouseX, double mouseY) {
        return mouseX >= windowX && mouseX <= windowX + WINDOW_WIDTH &&
                mouseY >= windowY && mouseY <= windowY + TITLE_BAR_HEIGHT;
    }

    private boolean isInWindow(double mouseX, double mouseY) {
        return mouseX >= windowX && mouseX <= windowX + WINDOW_WIDTH &&
                mouseY >= windowY && mouseY <= windowY + WINDOW_HEIGHT;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void setWindowPosition(float x, float y) {
        this.windowX = x;
        this.windowY = y;
        if (navigationBar != null) {
            navigationBar.setX(windowX + CONTENT_PADDING);
            navigationBar.setY(windowY + TITLE_BAR_HEIGHT + CONTENT_PADDING);
        }
        updatePanelPositions();
    }

    public float getWindowX() {
        return windowX;
    }

    public float getWindowY() {
        return windowY;
    }
}
